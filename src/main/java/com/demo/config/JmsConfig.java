package com.demo.config;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.ConnectionFactory;

@EnableJms
@Configuration
public class JmsConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        // 這裡使用一個假設的 ConnectionFactory 作為示例
        // 你需要使用你實際的消息代理的 ConnectionFactory
        MQQueueConnectionFactory factory = new MQQueueConnectionFactory();
        // 配置連接工廠（例如：設置 broker URL, 使用者名稱, 密碼等）
        return factory;
    }

}
