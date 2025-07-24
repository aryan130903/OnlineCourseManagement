package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.model.Category;
import com.project.onlinecoursemanagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository=categoryRepository;
    }

    public ResponseEntity<List<Category>> getAllCategory() {
        try {
            return new ResponseEntity<>(categoryRepository.findAll(), HttpStatus.OK);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> addCategory(Category category) {
        try {
            String normalizedName = category.getName().toLowerCase();

            if (categoryRepository.existsByName(normalizedName)) {
                return new ResponseEntity<>("Category already exists", HttpStatus.CONFLICT);
            }

            category.setName(normalizedName);
            categoryRepository.save(category);
            return new ResponseEntity<>("Success", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Failure", HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity<String> deleteCategory(Integer id) {

        if (!categoryRepository.existsById(id)) {
            return new ResponseEntity<>("Category not found", HttpStatus.NOT_FOUND);
        }

        try {
            categoryRepository.deleteById(id);
            return new ResponseEntity<>("Category deleted", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Delete failed", HttpStatus.BAD_REQUEST);
    }
}
