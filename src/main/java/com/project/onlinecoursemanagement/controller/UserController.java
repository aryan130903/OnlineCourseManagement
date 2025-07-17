package com.project.onlinecoursemanagement.controller;

import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping("allusers")
    public ResponseEntity<List<User>> getAllUser(){
        return userService.getAllUser();
    }

    @PostMapping("add")
    public ResponseEntity<String> addUser(@RequestBody User user){
        return userService.addUser(user);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id){
        return userService.deleteUser(id);
    }
}
