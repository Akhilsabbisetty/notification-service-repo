package com.ascloud;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class NotificationApp {

    @KafkaListener(topics = "orders")
    public void notifyUser(String msg) {
        System.out.println("Notification sent: " + msg);
    }

    @GetMapping("/notification")
    public String status() {
        return "Notification Service Running";
    }

    public static void main(String[] args) {
        SpringApplication.run(NotificationApp.class, args);
    }
}