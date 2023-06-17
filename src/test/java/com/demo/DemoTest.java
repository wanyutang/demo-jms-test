package com.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.BytesMessage;
import javax.jms.Session;

@SpringBootTest(classes = {ApiApplication.class})
@EnableJms
@Slf4j
public class DemoTest {

    @Value("${info.demo.val1}")
    String infoDemoVal1;

    @Autowired
    private JmsTemplate jmsTemplate;
    @Test
    void demo() {
       log.debug("infoDemoVal1 log res: {}", infoDemoVal1);
    }

    @Test
    void convertAndSend() {
        String xmlMessage = "<Message><Text>demo test msg</Text></Message>";
        byte[] binaryData = xmlMessage.getBytes();
        jmsTemplate.convertAndSend("DEV.QUEUE.1", binaryData);
        log.debug("log res: {}","Message sent successfully");
    }

    @Test
    void setObjectProperty() {
        jmsTemplate.send("DEV.QUEUE.1", new MessageCreator() {
            @Override
            public BytesMessage createMessage(Session session) throws javax.jms.JMSException {
                BytesMessage message = session.createBytesMessage();
                message.setObjectProperty("JMS_IBM_MQMD_MsgId", "demo123");
                return message;
            }
        });
    }

}
