package com.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ApiApplication.class})
@Slf4j
public class DemoTest {

    @Value("${info.demo.val1}")
    String infoDemoVal1;
    @Test
    public void demo() {
       log.debug("infoDemoVal1 log res: {}", infoDemoVal1);
    }

}
