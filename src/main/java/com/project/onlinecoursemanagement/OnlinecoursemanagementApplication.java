package com.project.onlinecoursemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OnlinecoursemanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlinecoursemanagementApplication.class, args);
	}

}
