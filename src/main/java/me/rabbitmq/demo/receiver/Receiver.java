package me.rabbitmq.demo.receiver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * me.rabbitmq.demo.receiver.Receiver
 *
 * @author Zhuang Jiabin
 * @version V1.0.0
 * @since 2020/12/10 10:54
 */
@Slf4j
@Component
public class Receiver {
    @Value("${spring.rabbitmq.listener.queues}")
    String[] queues;
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Bean
    public String[] queues() {
        return this.queues;
    }

    /**
     * 配置RabbitMQ消息监听容器工厂
     *
     * @param rabbitConnectionFactory RabbitMQ连接工厂，用于建立与RabbitMQ服务器的连接
     * @return 返回一个配置好的RabbitListenerContainerFactory实例，用于创建消息监听容器
     *
     * 该方法主要作用是创建并配置一个RabbitMQ消息监听容器工厂（RabbitListenerContainerFactory）的实例
     * 通过这个工厂，可以定制化消息的监听和处理行为例如，这里设置了连接工厂和预取计数
     */
    @Bean(name = "factory")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> factory(ConnectionFactory
                                                                                              rabbitConnectionFactory) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(rabbitConnectionFactory);
        factory.setPrefetchCount(5000);
        return factory;
    }

    // 监听指定队列的消息，使用工厂方法创建监听容器
    @RabbitListener(queues = "#{queues}", containerFactory = "factory")
    public void message2(String msg) {
        // 获取当前时间并格式化，用于日志记录
        var time = LocalDateTime.now().format(formatter);
        // 记录接收到的消息和当前时间
        log.info(" {}：{}", time, msg);

    }


}
