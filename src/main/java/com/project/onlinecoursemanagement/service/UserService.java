package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.LoginRequestDto;
import com.project.onlinecoursemanagement.dto.RegisterRequestDto;
import com.project.onlinecoursemanagement.exception.EmailAlreadyExistsException;
import com.project.onlinecoursemanagement.exception.RoleNotFoundException;
import com.project.onlinecoursemanagement.exception.UserNotFoundException;
import com.project.onlinecoursemanagement.model.Role;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.repository.RoleRepository;
import com.project.onlinecoursemanagement.repository.UserRepository;
import com.project.onlinecoursemanagement.security.jwt.JwtAuthenticationResponse;
import com.project.onlinecoursemanagement.security.jwt.JwtUtils;
import com.project.onlinecoursemanagement.service.Impl.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public void registerUser(RegisterRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + dto.getEmail());
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(encodedPassword);

        String requestedRole = dto.getRole();
        String roleToAssign = (requestedRole == null || requestedRole.isBlank()) ? "ROLE_STUDENT" : requestedRole;

        Role role = roleRepository.findByName(roleToAssign)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleToAssign));

        user.setRole(role);
        userRepository.save(user);
    }

    public ResponseEntity<List<User>> getAllUser() {
        try {
            return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
           e.printStackTrace();
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
    }

    public JwtAuthenticationResponse loginUser(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + loginRequestDto.getEmail()));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);

        return new JwtAuthenticationResponse(jwt);
    }
}
