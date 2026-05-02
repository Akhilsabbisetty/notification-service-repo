package com.ascloud;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class NotificationApp {

    @KafkaListener(topics = "orders")
    public void notifyUser(String msg) {
        System.out.println("Notification sent: " + msg);
    }

    @GetMapping("/notification")
    public Map<String, String> status() {
        return Map.of(
            "service", "notification-service",
            "status", "running"
        );
    }

    @PostMapping("/notification/invoice")
    public Map<String, String> sendInvoice(@RequestBody Map<String, Object> request) {
        String email = request.getOrDefault("email", "customer@example.com").toString();

        System.out.println("Invoice sent to " + email + " for demo order");

        return Map.of(
            "status", "sent",
            "email", email,
            "message", "Demo invoice sent successfully"
        );
    }

    public static void main(String[] args) {
        SpringApplication.run(NotificationApp.class, args);
    }
}