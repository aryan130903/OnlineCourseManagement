package com.project.onlinecoursemanagement.payment.service;

import com.project.onlinecoursemanagement.dto.CartSummaryDto;
import com.project.onlinecoursemanagement.dto.CourseSummaryDto;
import com.project.onlinecoursemanagement.exception.CartNotFoundException;
import com.project.onlinecoursemanagement.exception.EmptyCartException;
import com.project.onlinecoursemanagement.exception.UserNotFoundException;
import com.project.onlinecoursemanagement.mapper.CourseMapper;
import com.project.onlinecoursemanagement.model.Cart;
import com.project.onlinecoursemanagement.model.User;
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
public class RazorpayGatewayService implements PaymentGateway {

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
                    .orElseThrow(() -> new UserNotFoundException("User not found with email"));

            Cart cart = cartRepository.findByStudent(student)
                    .orElseThrow(() -> new CartNotFoundException("Cart not found for user"));

            if (cart.getCourses().isEmpty()) {
                throw new EmptyCartException("Cart is empty for user");
            }

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

        } catch (UserNotFoundException | CartNotFoundException | EmptyCartException exception) {
            throw exception;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
