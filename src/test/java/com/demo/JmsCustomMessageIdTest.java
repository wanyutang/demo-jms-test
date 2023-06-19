package com.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

@SpringBootTest
@EnableJms
public class JmsCustomMessageIdTest {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Queue queue;

    @Test
    public void sendMessageWithCustomMessageId() {
        String customMessageId = "demo123";
        //String customMessageId = java.util.UUID.randomUUID().toString();

        jmsTemplate.send(queue, session -> {
            TextMessage message;
            try {
                message = session.createTextMessage("Hello, World!");
                message.setJMSMessageID(customMessageId);
            } catch (JMSException e) {
                throw new RuntimeException("Failed to create JMS message.", e);
            }
            return message;
        });

        Message responseMessage = jmsTemplate.receiveSelected(queue, "JMSMessageID = '" + customMessageId + "'");

        if (responseMessage != null) {
            System.out.println("Sent Message: " + responseMessage.toString());
        } else {
            System.out.println("No response received.");
        }
    }
}
