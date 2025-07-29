package com.project.onlinecoursemanagement.payment.controller;
import com.project.onlinecoursemanagement.payment.service.PaymentVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentVerificationController {

    private final PaymentVerificationService verificationService;

    @PostMapping("/verify-payment")
    public ResponseEntity<String> verifyPayment(@RequestParam String paymentLinkId) {
        try {
            verificationService.verifyAndEnroll(paymentLinkId);
            return ResponseEntity.ok("Payment verified, user enrolled, cart cleared.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Verification failed: " + e.getMessage());
        }
    }
}
