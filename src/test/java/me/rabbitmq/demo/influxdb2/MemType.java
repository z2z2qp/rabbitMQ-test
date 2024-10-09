package me.rabbitmq.demo.influxdb2;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

/**
 * me.rabbitmq.demo.influxdb2.MemType
 *
 * @author Zhuang Jiabin
 * @version V1.0.0
 * @since 2021/8/20 14:26
 */
public class MemType {
    private String host;
    private String name;
    private String field;
    private double value;
    private Instant time;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "MemType{" +
                "host='" + host + '\'' +
                ", name='" + name + '\'' +
                ", field='" + field + '\'' +
                ", value=" + value +
                ", time=" + time +
                '}';
    }
}
