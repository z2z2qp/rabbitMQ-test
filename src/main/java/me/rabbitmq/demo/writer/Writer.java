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
    /**
     * 初始化RabbitMQ连接和通道
     * 该方法在当前类实例化后自动调用，用于建立与RabbitMQ服务器的连接和通道，以及声明队列
     *
     * @throws IOException 如果在建立连接或声明队列过程中遇到I/O错误
     * @throws TimeoutException 如果在建立连接或声明队列过程中超时
     */
    public void init() throws IOException, TimeoutException {
        // 标志变量，用于指示是否已完成初始化
        flag = true;

        // 创建一个新的连接工厂实例
        var factory = new ConnectionFactory();

        // 设置连接工厂的主机地址、用户名、密码和端口
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setPort(port);

        // 启用自动恢复和拓扑恢复，以提高连接的可靠性
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);

        // 建立到RabbitMQ服务器的连接
        connection = factory.newConnection();

        // 创建一个通道，大部分API操作都是通过通道完成的
        channel = connection.createChannel();

        // 声明队列，确保队列在通道上被创建，队列列表在代码中以数组或列表形式提供
        channel.queueDeclare(queues, true, false, false, null);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 当标志为真时，持续执行以下代码
        while (flag) {
            // 生成唯一的消息内容，包含一个UUID和当前时间戳
            var message = UUID.randomUUID() + ":" + System.currentTimeMillis();
            // 打印消息内容，以便于跟踪和调试
            System.out.println(message);
            // 将消息发布到指定的队列中
            channel.basicPublish("", queues, null, message.getBytes(StandardCharsets.UTF_8));
            // 尝试暂停1秒，以控制消息发送速率
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException _) {
                // 如果线程被中断，忽略中断信号，继续执行
            }
        }
    }

    /**
     * 重写接口中的方法，当Spring应用上下文关闭时触发
     * 此方法的目的是正确清理和关闭与消息队列服务相关的资源，如通道和连接，以避免资源泄露
     *
     * @param contextClosedEvent 有关应用上下文关闭的事件对象，使用@NotNull注解确保事件对象非空
     */
    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent contextClosedEvent) {
        // 设置标志变量为false，可能用于标记某种操作状态的变化
        flag = false;

        // 检查消息队列通道是否存在，如果存在则尝试关闭通道
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                // 如果在关闭通道过程中发生异常，打印异常堆栈信息进行调试
                e.printStackTrace();
            }
        }

        // 检查消息队列连接是否存在，如果存在则尝试关闭连接
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                // 如果在关闭连接过程中发生异常，打印异常堆栈信息进行调试
                e.printStackTrace();
            }
        }
    }

}
