package com.project.onlinecoursemanagement.service.impl;

import com.project.onlinecoursemanagement.dto.CategoryDto;
import com.project.onlinecoursemanagement.exception.AlreadyInUseException;
import com.project.onlinecoursemanagement.exception.CategoryNotFoundException;
import com.project.onlinecoursemanagement.mapper.CategoryMapper;
import com.project.onlinecoursemanagement.model.Category;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.repository.CategoryRepository;
import com.project.onlinecoursemanagement.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

     private final CategoryRepository categoryRepository;


    @Cacheable(value = "allCategories")
    public List<CategoryDto> getAllCategory() {
        List<Category> categories = categoryRepository.findAll();

        if (categories.isEmpty()) {
            throw new CategoryNotFoundException("No categories found");
        }

        return categories.stream()
                .map(CategoryMapper::toSummaryDto)
                .collect(Collectors.toList());
    }



    @CacheEvict(value = "allCategories", allEntries = true)
    public String addCategory(CategoryDto categoryDto) {
        String normalizedName = categoryDto.getName().trim().toLowerCase();

        if (categoryRepository.existsByName(normalizedName)) {
            throw new AlreadyInUseException("Category already exists");
        }

        Category category = new Category();
        category.setName(normalizedName);

        categoryRepository.save(category);
        return "Success";
    }


    @CacheEvict(value = "allCategories", allEntries = true)
    public String deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        if (!category.getCourses().isEmpty()) {
            throw new AlreadyInUseException("Cannot delete category. It's in use by some course");
        }

        categoryRepository.deleteById(id);
        return "Category deleted";
    }


}
