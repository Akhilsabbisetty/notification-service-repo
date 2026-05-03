package com.ascloud;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class NotificationApp {

    private final JavaMailSender mailSender;

    public NotificationApp(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @KafkaListener(topics = "orders")
    public void notifyUser(String msg) {
        System.out.println("Notification received: " + msg);
    }

    @GetMapping("/notification")
    public Map<String, String> status() {
        return Map.of("service", "notification-service", "status", "running");
    }

    @PostMapping("/notification/invoice")
    public Map<String, String> sendInvoice(@RequestBody InvoiceRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.email);
        message.setSubject("SneakHive Invoice - Order " + request.orderId);
        message.setText(
                "Hello,\n\n" +
                "Thank you for shopping with SneakHive.\n\n" +
                "Order ID: " + request.orderId + "\n" +
                "Amount: $" + request.amount + "\n" +
                "Status: Paid\n\n" +
                "This is your invoice confirmation.\n\n" +
                "Regards,\n" +
                "SneakHive Team"
        );

        mailSender.send(message);

        return Map.of(
                "status", "sent",
                "message", "Invoice sent successfully to " + request.email
        );
    }

    public static void main(String[] args) {
        SpringApplication.run(NotificationApp.class, args);
    }
}

class InvoiceRequest {
    public String orderId;
    public String email;
    public Double amount;
}
