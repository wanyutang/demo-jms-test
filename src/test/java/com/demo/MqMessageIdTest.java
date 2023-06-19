package com.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
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
        BytesMessage receivedMessage = (BytesMessage) jmsTemplate.receive(QUEUE_NAME);

        assertNotNull(receivedMessage, "Received message is null");

        byte[] messageIdBytes = (byte[]) receivedMessage.getObjectProperty("JMS_IBM_MQMD_MsgId");
        assertNotNull(messageIdBytes, "JMS_IBM_MQMD_MsgId property is null");

        String messageId = new String(messageIdBytes, StandardCharsets.UTF_8);

        // 驗證 MsgId 是否與預期的自定義值匹配
        assertEquals(customMessageId, messageId);

    }

}
