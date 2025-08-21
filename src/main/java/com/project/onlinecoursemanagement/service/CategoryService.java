package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.CategoryDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryService {

    ResponseEntity<List<CategoryDto>> getAllCategory();

    ResponseEntity<?> addCategory(CategoryDto categoryDto);

    ResponseEntity<String> deleteCategory(Integer id);
}
