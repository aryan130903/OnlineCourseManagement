package com.project.onlinecoursemanagement.controller;

import com.project.onlinecoursemanagement.model.Category;
import com.project.onlinecoursemanagement.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService){
        this.categoryService=categoryService;
    }

    @GetMapping("allcategory")
    public ResponseEntity<List<Category>> getAllCategory(){
        return categoryService.getAllCategory();
    }
    @PostMapping("add")
    public ResponseEntity<String> addCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer id){
        return categoryService.deleteCategory(id);
    }
}
