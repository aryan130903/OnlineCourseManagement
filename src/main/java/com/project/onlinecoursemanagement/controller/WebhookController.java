package com.project.onlinecoursemanagement.controller;

import com.project.onlinecoursemanagement.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/payment-webhook")
    public ResponseEntity<String> handlePaymentWebhook(@RequestBody String payload) {
        try {
            webhookService.processPaymentWebhook(payload);
            return ResponseEntity.ok("User enrolled and cart cleared");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error processing webhook: " + e.getMessage());
        }
    }
}
