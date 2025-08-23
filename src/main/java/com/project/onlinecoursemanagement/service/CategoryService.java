package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.CategoryDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAllCategory();

    String addCategory(CategoryDto categoryDto);

    String deleteCategory(Integer id);
}
