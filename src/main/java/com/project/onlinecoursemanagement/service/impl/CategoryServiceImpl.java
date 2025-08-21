package com.project.onlinecoursemanagement.service.impl;

import com.project.onlinecoursemanagement.dto.CategoryDto;
import com.project.onlinecoursemanagement.exception.CategoryNotFoundException;
import com.project.onlinecoursemanagement.mapper.CategoryMapper;
import com.project.onlinecoursemanagement.model.Category;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.repository.CategoryRepository;
import com.project.onlinecoursemanagement.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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


    public ResponseEntity<List<CategoryDto>> getAllCategory() {
        try {
            List<Category> categories = categoryRepository.findAll();

            if (categories.isEmpty()){
                throw new CategoryNotFoundException("No categories found");
            }

            List<CategoryDto> dtoList = categories.stream()
                    .map(CategoryMapper::toSummaryDto)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(dtoList, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
    }


    public ResponseEntity<?> addCategory(CategoryDto categoryDto) {
        try {
                String normalizedName = categoryDto.getName().toLowerCase();

                if (categoryRepository.existsByName(normalizedName)) {
                    return new ResponseEntity<>("Category already exists", HttpStatus.CONFLICT);
                }

                Category category = new Category();
                category.setName(normalizedName);

                categoryRepository.save(category);
            return new ResponseEntity<>("Success", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Failure", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> deleteCategory(Integer id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new CategoryNotFoundException("Category not found"));

        List<Course> courses = category.getCourses();

        if (!courses.isEmpty()){
            return new ResponseEntity<>("Cannot delete category.It's in use by some course", HttpStatus.NOT_FOUND);
        }

//        if (!categoryRepository.existsById(id)) {
//            return new ResponseEntity<>("Category not found", HttpStatus.NOT_FOUND);
//        }

        try {
            categoryRepository.deleteById(id);
            return new ResponseEntity<>("Category deleted", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Delete failed", HttpStatus.BAD_REQUEST);
    }

}
