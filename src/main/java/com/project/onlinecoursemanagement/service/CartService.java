package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.CourseSummaryDto;

import java.util.Set;

public interface CartService {

    void addToCart(Long courseId, String email);

    void removeFromCart(Long courseId, String email);

    Set<CourseSummaryDto> viewCart(String email);
}
