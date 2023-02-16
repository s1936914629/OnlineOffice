package cn.org.sqx.emos.wx.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @auther: sqx
 * @Date: 2023-02-09
 */
@Configuration
public class RabbitMQConfig {
    @Bean
    public ConnectionFactory getFactory(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("124.220.169.221");  //RabbitMQ的主机所在的IP地址
        factory.setPort(5672);  //RabbitMQ的端口号
        return factory;
    }
}
