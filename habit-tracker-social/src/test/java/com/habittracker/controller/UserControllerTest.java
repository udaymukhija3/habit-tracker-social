package com.habittracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habittracker.dto.UserDTO;
import com.habittracker.model.Role;
import com.habittracker.model.User;
import com.habittracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(Role.USER);
        return user;
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetProfile() throws Exception {
        // Given
        User user = createTestUser();
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testUpdateProfile() throws Exception {
        // Given
        User user = createTestUser();
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Updated");
        userDTO.setLastName("Name");
        userDTO.setEmail("updated@example.com");

        User updatedUser = createTestUser();
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("Name");
        updatedUser.setEmail("updated@example.com");

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    @WithMockUser
    public void testSearchUsers() throws Exception {
        // Given
        User user1 = createTestUser();
        User user2 = createTestUser();
        user2.setId(2L);
        user2.setUsername("testuser2");
        List<User> users = Arrays.asList(user1, user2);

        when(userService.searchUsers("test")).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/users/search")
                .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[1].username").value("testuser2"));
    }

    @Test
    @WithMockUser
    public void testGetUser() throws Exception {
        // Given
        User user = createTestUser();
        when(userService.findById(1L)).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser
    public void testGetUserNotFound() throws Exception {
        // Given
        when(userService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }
}

