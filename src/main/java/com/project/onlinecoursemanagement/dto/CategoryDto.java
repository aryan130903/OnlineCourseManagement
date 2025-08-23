package com.project.onlinecoursemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Integer id;
    @NotBlank(message = "Category name must not be empty")
    @Size(min = 2, message = "Category name must be at least 2 characters")
    private String name;
}
