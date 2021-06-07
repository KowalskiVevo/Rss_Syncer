package com.example.demo.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    @KafkaListener(topics = "msg")
    public void msgListener(String msg) {
        System.out.println("**************************************************************");
        System.out.println(msg);
    }
}
