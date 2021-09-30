package me.rabbitmq.demo.influxdb2;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

/**
 * me.rabbitmq.demo.influxdb2.Mem
 *
 * @author Zhuang Jiabin
 * @version V1.0.0
 * @since 2021/8/19 10:37
 */

@Measurement(name = "mem")
public class Mem{
    @Column(tag = true)
    private String host;
    @Column(tag = true)
    private String name;
    @Column(name = "use")
    private Double use;
    @Column(name = "no")
    private Double no;
    @Column(timestamp = true)
    private Instant time;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Double getUse() {
        return use;
    }

    public void setUse(Double use) {
        this.use = use;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Double getNo() {
        return no;
    }

    public void setNo(Double no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Mem{" +
                "host='" + host + '\'' +
                ", use=" + use +
                ", time=" + time +
                ", no=" + no +
                ", name=" + name +
                '}';
    }
}
