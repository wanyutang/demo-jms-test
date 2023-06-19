package com.demo;

import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CustomMqMessageIdTest {

    private static final String QUEUE_NAME = "DEV.QUEUE.1";
    private static final String HOST_NAME = "10.20.30.196";
    private static final int PORT = 1414;
    private static final String CHANNEL = "DEV.ADMIN.SVRCONN";
    private static final String USER = "admin";
    private static final String PASSWORD = "passw0rd";

    private MQQueueManager queueManager;
    private MQQueue queue;

    @BeforeEach
    public void setup() throws MQException, IOException {
        MQEnvironment.hostname = HOST_NAME;
        MQEnvironment.port = PORT;
        MQEnvironment.channel = CHANNEL;
        MQEnvironment.userID = USER;
        MQEnvironment.password = PASSWORD;

        queueManager = new MQQueueManager("QM1");
        queue = queueManager.accessQueue(QUEUE_NAME, MQConstants.MQOO_OUTPUT);

        // 發送一筆測試訊息到佇列中
        MQPutMessageOptions putOptions = new MQPutMessageOptions();
        MQMessage message = new MQMessage();
        message.writeString("Test Message");
        queue.put(message, putOptions);
    }


    @AfterEach
    public void cleanup() throws MQException {
        if (queue != null) {
            queue.close();
        }
        if (queueManager != null) {
            queueManager.disconnect();
        }
    }

    @Test
    public void sendMessageWithCustomMessageId() throws IOException, MQException {
        MQPutMessageOptions putOptions = new MQPutMessageOptions();
        MQMessage message = new MQMessage();

        // 設置自定義的 MessageId
        String customMessageId = "YourCustomId123";
        message.messageId = customMessageId.getBytes();
        message.writeString("Hello, World!");

        queue.put(message, putOptions);

        // 驗證自訂的 MessageId 是否正確
        MQGetMessageOptions getOptions = new MQGetMessageOptions();
        getOptions.options = MQConstants.MQGMO_WAIT | MQConstants.MQGMO_CONVERT;
        getOptions.waitInterval = MQConstants.MQWI_UNLIMITED;

        MQMessage receivedMessage = new MQMessage();
        queue.get(receivedMessage, getOptions);
        String receivedMessageId = new String(receivedMessage.messageId).trim();
        assertEquals(customMessageId, receivedMessageId);
    }
}
