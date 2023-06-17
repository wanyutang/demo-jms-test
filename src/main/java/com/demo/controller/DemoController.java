package com.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @PostMapping("demo1")
    @ResponseBody
    public Object demo1(@RequestBody Object req) {
        return "DemoController.demo return msg demo";
    }
}
