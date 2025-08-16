package com.project.onlinecoursemanagement.mapper;

import com.project.onlinecoursemanagement.dto.CategoryDto;
import com.project.onlinecoursemanagement.model.Category;

public class CategoryMapper {

    public static CategoryDto toSummaryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}
