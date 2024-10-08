package me.rabbitmq.demo.influxdb2;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
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

    /**
     * 在所有测试方法执行之前初始化一次
     * 此方法用于设置测试中需要使用的全局变量
     */
    @BeforeAll
    public static void before() {
        // 设置token，用于认证
        token = "PWfq-mvweGFUZXAbocR-a__-QXHYB6TfJbRixjFMuox3h1SRvIoIwd1L7loFZkWMsxc_hZXJcL58s6UcYUmjMQ==";
        // 设置存储桶名称，用于存储数据
        bucket = "raw";
        // 设置组织名称
        org = "heda";
        // 创建InfluxDB客户端，用于连接数据库
        client = InfluxDBClientFactory.create("http://localhost:8086", token.toCharArray());
    }

    // 测试写入数据行的功能
    @Test
    public void writeLine() {
        // 定义要写入的数据字符串，包含测量、主机名、名称、使用率和编号
        var data = "mem,host=host1,name=1 use=23.2,no=56";
        // 使用try-with-resources确保写入资源在使用后正确关闭
        try (var write = client.makeWriteApi()) {
            // 通过API写入数据到指定的bucket和组织中，使用纳秒级的精度
            write.writeRecord(bucket, org, WritePrecision.NS, data);
        }
    }

    @Test
    public void writePoint() {
        // 创建一个测量名为"mem"的点对象
        var point = Point.measurement("mem")
                .addTag("host", "host2") // 添加主机名为"host2"的标签
                .addTag("name", "2") // 添加名为"2"的标签
                .addField("use", 25.2) // 添加名为"use"的浮点字段，值为25.2
                .addField("no", 5.6) // 添加名为"no"的浮点字段，值为5.6
                .time(Instant.now(), WritePrecision.NS); // 设置当前纳秒时间戳
        // 使用try-with-resources确保WriteApi资源在使用后正确关闭
        try (var write = client.makeWriteApi()) {
            // 将点写入到指定的bucket和org中
            write.writePoint(bucket, org, point);
        }
    }

    @Test
    public void writePOJO() {
        // 创建一个Mem对象并设置其属性，用于后续写入操作
        var mem = new Mem();
        mem.setHost("host3");
        mem.setTime(Instant.now());
        mem.setNo(0.09);
        mem.setUse(26.2);

        // 使用try-with-resources确保WriteApi资源在使用后被正确关闭
        try (var write = client.makeWriteApi()) {
            // 将Mem对象写入到指定的bucket和org中，使用纳秒级的写入精度
            write.writeMeasurement(bucket, org, WritePrecision.NS, mem);
        }
    }

    @Test
    // 执行查询操作
    public void query() {
        // 构建查询语句，从指定桶中获取最近10天的数据
        var query = String.format("from(bucket: \"%s\") |> range(start: -10d)", bucket);
        // 执行查询并解析为MemType列表
        var memList = client.getQueryApi().query(query, org, MemType.class);
        // 执行查询并打印每个记录的值
        var tables = client.getQueryApi().query(query, org);
        tables.forEach(it -> it.getRecords().forEach(item -> System.out.println(item.getValues())));

        // 初始化一个映射，用于根据主机、名称和时间对MemType对象进行分组
        var map = new HashMap<String, List<MemType>>();
        memList.forEach(it -> {
            var key = it.getHost() + it.getName() + it.getTime();
            System.out.println(key);
            var types = map.computeIfAbsent(key, _ -> new ArrayList<>());
            types.add(it);
        });
        // 创建一个列表，用于存储处理后的Mem对象
        var list = new ArrayList<>();
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
        // 打印处理后的Mem对象列表
        System.out.println(list);
    }

    @Test
    // 查询2函数，用于从指定的bucket中查询数据
    public void query2() {
        // 格式化查询语句，从指定的bucket中查询过去10天的数据
        String query = String.format("from(bucket: \"%s\") |> range(start: -10d)", bucket);
        // 使用查询API执行查询操作，传入查询语句、组织信息和目标类类型，并通过回调函数处理结果
        client.getQueryApi().query(query, org, MemType.class, (_, t) -> System.out.println(t));
    }

}
