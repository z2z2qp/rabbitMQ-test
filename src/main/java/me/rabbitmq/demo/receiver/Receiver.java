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

    @Bean(name = "factory")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> factory(ConnectionFactory
                                                                                          rabbitConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(rabbitConnectionFactory);
        factory.setPrefetchCount(5000);
        return factory;
    }

    @RabbitListener(queues = "#{queues}", containerFactory = "factory")
    public void message2(String msg) {
        String time = LocalDateTime.now().format(formatter);
        log.info(" {}：{}", time, msg);

    }


}
