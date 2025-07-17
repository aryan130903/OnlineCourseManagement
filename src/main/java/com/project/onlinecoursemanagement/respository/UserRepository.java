package com.project.onlinecoursemanagement.respository;

import com.project.onlinecoursemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRepository extends JpaRepository<User,Integer> {
}
