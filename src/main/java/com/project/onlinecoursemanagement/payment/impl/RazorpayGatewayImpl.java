package com.project.onlinecoursemanagement.payment.impl;

import com.project.onlinecoursemanagement.dto.CartSummaryDto;
import com.project.onlinecoursemanagement.dto.CourseSummaryDto;
import com.project.onlinecoursemanagement.model.Cart;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.mapper.CourseMapper;
import com.project.onlinecoursemanagement.payment.PaymentGateway;
import com.project.onlinecoursemanagement.repository.CartRepository;
import com.project.onlinecoursemanagement.repository.UserRepository;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service("razorpayGateway")
@RequiredArgsConstructor
public class RazorpayGatewayImpl implements PaymentGateway {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    @Override
    public CartSummaryDto handleCheckout(String username) {
        try {
            User student = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Cart cart = cartRepository.findByStudent(student)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));

            List<CourseSummaryDto> courses = cart.getCourses().stream()
                    .map(CourseMapper::toSummaryDto)
                    .collect(Collectors.toList());

            BigDecimal totalAmount = courses.stream()
                    .map(CourseSummaryDto::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            RazorpayClient razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);

            JSONObject paymentRequest = new JSONObject();
            paymentRequest.put("amount", totalAmount.multiply(BigDecimal.valueOf(100)).intValue()); // in paise
            paymentRequest.put("currency", "INR");
            paymentRequest.put("description", "Payment for enrolled courses");

            JSONObject customer = new JSONObject();
            customer.put("name", student.getUsername());
            customer.put("email", student.getEmail());
            paymentRequest.put("customer", customer);

            JSONObject notify = new JSONObject();
            notify.put("email", true);
            paymentRequest.put("notify", notify);

            // Add student email in notes so we can verify in webhook or after payment
            JSONObject notes = new JSONObject();
            notes.put("email", student.getEmail());
            paymentRequest.put("notes", notes);

            PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentRequest);

            return new CartSummaryDto(
                    totalAmount,
                    paymentLink.get("short_url"),
                    paymentLink.get("id"),
                    courses
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay payment", e);
        }
    }
}
