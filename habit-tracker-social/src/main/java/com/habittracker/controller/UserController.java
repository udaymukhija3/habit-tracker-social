package com.habittracker.controller;

import com.habittracker.dto.UserDTO;
import com.habittracker.model.User;
import com.habittracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody UserDTO userDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.get());
    }
}
