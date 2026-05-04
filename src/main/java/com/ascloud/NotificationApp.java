package com.ascloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.*;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class NotificationApp {

    private final SnsClient snsClient;

    @Value("${aws.sns.topic-arn}")
    private String topicArn;

    public NotificationApp(@Value("${aws.region:ap-south-1}") String awsRegion) {
        this.snsClient = SnsClient.builder()
                .region(Region.of(awsRegion))
                .build();
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

        String subject = "SneakHive Invoice - Order " + request.orderId;

        String message =
                "Hello,\n\n" +
                "Thank you for shopping with SneakHive.\n\n" +
                "Order ID: " + request.orderId + "\n" +
                "Customer Email: " + request.email + "\n" +
                "Amount: $" + request.amount + "\n" +
                "Status: Paid\n\n" +
                "Regards,\n" +
                "SneakHive Team";

        snsClient.publish(PublishRequest.builder()
                .topicArn(topicArn)
                .subject(subject)
                .message(message)
                .build());

        return Map.of(
                "status", "sent",
                "message", "Invoice details published to SNS"
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