package com.project.onlinecoursemanagement.payment.service;

import com.project.onlinecoursemanagement.dto.CartSummaryDto;
import com.project.onlinecoursemanagement.payment.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentGateway paymentGateway;

    public CartSummaryDto handleCheckout(String username) {
        return paymentGateway.handleCheckout(username);
    }
}
