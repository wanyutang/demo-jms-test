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
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
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
                byte[] messageId = "demo123".getBytes(); // 將字串轉換為位元組陣列
                message.setObjectProperty("JMS_IBM_MQMD_MsgId", messageId);
                return message;
            }
        });
    }

    @Test
    public void testMessageProperties() throws InterruptedException {
        String queueName = "DEV.QUEUE.1"; // 替換為實際的 Queue 名稱

        // 發送消息到 MQ
        jmsTemplate.send(queueName, new MessageCreator() {
            @Override
            public BytesMessage createMessage(Session session) throws JMSException {
                BytesMessage message = session.createBytesMessage();
                byte[] messageId = "demo123".getBytes(); // 將字串轉換為位元組陣列
                message.setObjectProperty("JMS_IBM_MQMD_MsgId", messageId);
                return message;
            }
        });

        // 等待一段時間以使MQ處理消息
        Thread.sleep(2000);

        // 接收消息並列出其屬性
        Message receivedMessage = jmsTemplate.receive(queueName);

        if (receivedMessage != null) {
            try {
                Enumeration<?> propertyNames = receivedMessage.getPropertyNames();
                while (propertyNames.hasMoreElements()) {
                    String propertyName = (String) propertyNames.nextElement();
                    Object propertyValue = receivedMessage.getObjectProperty(propertyName);
                    log.debug("log res: Property Name: {}, Property Value: {}", propertyName, propertyValue);
                }
            } catch (JMSException e) {
                e.printStackTrace();
                log.debug("log res: Error while fetching properties from the message.");
            }
        } else {
            log.debug("log res: Message is null");
        }
    }

    @Test
    public void testCustomMessageId() {
        String queueName = "DEV.QUEUE.1";
        byte[] customMessageId = "abcd1234".getBytes();
        String xmlMessage = "<Message><Text>demo test msg</Text></Message>";

        // Send a message with custom messageId
        jmsTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                BytesMessage message = session.createBytesMessage();
                message.writeBytes(xmlMessage.getBytes());
                message.setObjectProperty("JMS_IBM_MQMD_MsgId", customMessageId);
                return message;
            }
        });

        // Receive the message
        Message receivedMessage = jmsTemplate.receive(queueName);

        if (receivedMessage != null) {
            try {
                Enumeration<?> propertyNames = receivedMessage.getPropertyNames();
                while (propertyNames.hasMoreElements()) {
                    String propertyName = (String) propertyNames.nextElement();
                    Object propertyValue = receivedMessage.getObjectProperty(propertyName);
                    log.debug("log res: Property Name: {}, Property Value: {}", propertyName, propertyValue);
                }
            } catch (JMSException e) {
                e.printStackTrace();
                log.error("log res: Error while fetching properties from the message.");
            }
        } else {
            log.info("log res: Message is null");
        }
    }




}
