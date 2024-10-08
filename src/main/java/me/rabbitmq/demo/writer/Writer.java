package me.rabbitmq.demo.writer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * me.rabbitmq.demo.Writer.Writer
 *
 * @author Zhuang Jiabin
 * @version V1.0.0
 * @since 2021/11/12 16:23
 */
//@Component
public class Writer implements ApplicationRunner, ApplicationListener<ContextClosedEvent> {


    // spring:
    // application:
    // name: hdiot_mq_demo
    // rabbitmq:
    // host: 192.168.100.78
    // port: 5672
    // username: will
    // password: will
    // listener:
    // queues: SCADA7

    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.listener.queues}")
    private String queues;

    private Connection connection;
    private Channel channel;
    private boolean flag;

    @PostConstruct
    public void init() throws IOException, TimeoutException {
        flag = true;
        var factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setPort(port);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(queues, true, false, false, null);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (flag) {
            var message = UUID.randomUUID() + ":" + System.currentTimeMillis();
            System.out.println(message);
            channel.basicPublish("", queues, null, message.getBytes(StandardCharsets.UTF_8));
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException _) {

            }
        }
    }

    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent contextClosedEvent) {
        flag = false;
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
