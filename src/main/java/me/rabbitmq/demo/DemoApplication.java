package me.rabbitmq.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {


    /**
     * 主函数入口
     * 使用Spring Boot框架启动应用程序
     *
     * @param args 命令行参数，用于传递给应用程序
     */
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
