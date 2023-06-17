package com.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

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
        String message = "demo test msg";
        jmsTemplate.convertAndSend("DEV.QUEUE.1", message);
        log.debug("log res: {}","Message sent successfully");
    }


}
