package me.rabbitmq.demo.influxdb2;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * me.rabbitmq.demo.influxdb2.Influxdb2Test
 *
 * @author Zhuang Jiabin
 * @version V1.0.0
 * @since 2021/8/19 10:07
 */
public class Influxdb2Test {

    private static InfluxDBClient client;
    private static String bucket;
    private static String token;
    private static String org;

    @BeforeAll
    public static void before() {
        token = "PWfq-mvweGFUZXAbocR-a__-QXHYB6TfJbRixjFMuox3h1SRvIoIwd1L7loFZkWMsxc_hZXJcL58s6UcYUmjMQ==";
        bucket = "raw";
        org = "heda";
        client = InfluxDBClientFactory.create("http://localhost:8086", token.toCharArray());
    }

    @Test
    public void writeLine() {
        String data = "mem,host=host1,name=1 use=23.2,no=56";
        try (WriteApi write = client.getWriteApi()) {
            write.writeRecord(bucket, org, WritePrecision.NS, data);
        }
    }

    @Test
    public void writePoint() {
        Point point = Point.measurement("mem")
                .addTag("host", "host2")
                .addTag("name", "2")
                .addField("use", 25.2)
                .addField("no", 5.6)
                .time(Instant.now(), WritePrecision.NS);
        try (WriteApi write = client.getWriteApi()) {
            write.writePoint(bucket, org, point);
        }
    }

    @Test
    public void writePOJO() {
        Mem mem = new Mem();
        mem.setHost("host3");
        mem.setTime(Instant.now());
        mem.setNo(0.09);
        mem.setUse(26.2);
        try (WriteApi write = client.getWriteApi()) {
            write.writeMeasurement(bucket, org, WritePrecision.NS, mem);
        }
    }

    @Test
    public void query() {
        String query = String.format("from(bucket: \"%s\") |> range(start: -10d)", bucket);
        List<MemType> memList = client.getQueryApi().query(query, org, MemType.class);
        List<FluxTable> tables = client.getQueryApi().query(query, org);
        tables.forEach(it -> it.getRecords().forEach(item -> System.out.println(item.getValues())));

        HashMap<String, List<MemType>> map = new HashMap<>();
        memList.forEach(it -> {
            String key = it.getHost() + it.getName() + it.getTime();
            System.out.println(key);
            List<MemType> types = map.computeIfAbsent(key, _key -> new ArrayList<>());
            types.add(it);
        });
        List<Mem> list = new ArrayList<>();
        map.values().forEach(v -> {
            Mem mem = new Mem();
            v.forEach(it -> {
                if (mem.getTime() == null) {
                    mem.setName(it.getName());
                    mem.setTime(it.getTime());
                    mem.setHost(it.getHost());
                    list.add(mem);
                }
                if (it.getField().equalsIgnoreCase("no")) {
                    mem.setNo(it.getValue());
                }
                if (it.getField().equalsIgnoreCase("use")) {
                    mem.setUse(it.getValue());
                }
            });
        });
        System.out.println(list);
    }

    @Test
    public void query2() {
        String query = String.format("from(bucket: \"%s\") |> range(start: -10d)", bucket);
        client.getQueryApi().query(query, org, MemType.class, (c, t) -> System.out.println(t));

    }

}
