package com.project.onlinecoursemanagement.respository;


import com.project.onlinecoursemanagement.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
    boolean existsByName(String name);
}
