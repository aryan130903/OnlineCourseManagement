package com.project.onlinecoursemanagement.payment;

import com.project.onlinecoursemanagement.dto.CartSummaryDto;

public interface PaymentGateway {
    CartSummaryDto handleCheckout(String username);
}
