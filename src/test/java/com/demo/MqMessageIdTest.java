package com.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EnableJms
@TestMethodOrder(OrderAnnotation.class)
public class MqMessageIdTest {

    @Autowired
    private JmsTemplate jmsTemplate;

    private static final String QUEUE_NAME = "DEV.QUEUE.1";
    private static String customMessageId;

    @Test
    @Order(1)
    public void sendMessageWithCustomMsgId() {
        customMessageId = UUID.randomUUID().toString().replace("-", "");

        jmsTemplate.send(QUEUE_NAME, session -> {
            BytesMessage message = session.createBytesMessage();
            message.writeBytes("<Message><Text>demo test msg</Text></Message>".getBytes(StandardCharsets.UTF_8));
            message.setObjectProperty("JMS_IBM_MQMD_MsgId", customMessageId.getBytes(StandardCharsets.UTF_8));
            return message;
        });
    }

    @Test
    @Order(2)
    public void receiveMessageAndVerifyMsgId() throws JMSException {
        Message receivedMessage = jmsTemplate.receive(QUEUE_NAME);
        if (receivedMessage != null) {
            if (receivedMessage instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) receivedMessage;
                String messageText = textMessage.getText();
                System.out.println("Received message: " + messageText);
            } else if (receivedMessage instanceof BytesMessage) {
                BytesMessage bytesMessage = (BytesMessage) receivedMessage;
                byte[] buffer = new byte[1024];
                int bytesRead;
                StringBuilder messageContent = new StringBuilder();
                while ((bytesRead = bytesMessage.readBytes(buffer)) != -1) {
                    messageContent.append(new String(buffer, 0, bytesRead));
                }
                System.out.println("Received BytesMessage content: " + messageContent.toString());
            } else {
                System.out.println("Received message of unknown type: " + receivedMessage.getClass().getName());
            }
        } else {
            System.out.println("No message received");
        }
    }

}
