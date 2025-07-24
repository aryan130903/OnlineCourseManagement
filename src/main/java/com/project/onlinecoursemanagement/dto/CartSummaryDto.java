package com.project.onlinecoursemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CartSummaryDto {
    private BigDecimal totalAmount;
    private String paymentLink;
    private List<CourseSummaryDto> courses;
}
