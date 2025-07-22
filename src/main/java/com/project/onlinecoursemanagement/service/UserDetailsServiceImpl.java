package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user=userRepository.findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with Username: "+username));
        return UserDetailsImpl.build(user);
    }
}
