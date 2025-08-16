package com.project.onlinecoursemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartSummaryDto {
    private BigDecimal totalAmount;
    private String paymentLink;
    private String paymentLinkId;
    private List<CourseSummaryDto> courses;
}