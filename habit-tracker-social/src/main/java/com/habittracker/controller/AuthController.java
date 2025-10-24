package com.habittracker.controller;

import com.habittracker.dto.JwtResponse;
import com.habittracker.dto.LoginRequest;
import com.habittracker.dto.UserDTO;
import com.habittracker.model.User;
import com.habittracker.service.UserService;
import com.habittracker.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        User user = (User) authentication.getPrincipal();
        
        // Update last login
        userService.updateLastLogin(user.getId());
        
        return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getUsername(), 
                                               user.getEmail(), user.getRole()));
    }
    
    @Operation(summary = "User registration", description = "Register a new user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Username or email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        if (userService.existsByUsername(userDTO.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        
        if (userService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        
        User user = new User(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPassword());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        
        User createdUser = userService.createUser(user);
        
        return ResponseEntity.ok("User registered successfully!");
    }
}
