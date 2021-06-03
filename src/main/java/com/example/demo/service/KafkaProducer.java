package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    // KafkaTemplate<K, V>

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrder(String jsonObject) {
        kafkaTemplate.send("msg", jsonObject);
    }
}
