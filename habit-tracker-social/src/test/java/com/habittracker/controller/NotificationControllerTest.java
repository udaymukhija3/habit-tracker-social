package com.habittracker.controller;

import com.habittracker.model.Notification;
import com.habittracker.model.NotificationStatus;
import com.habittracker.model.NotificationType;
import com.habittracker.model.Role;
import com.habittracker.model.User;
import com.habittracker.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
        return user;
    }

    private Notification createTestNotification(Long id, NotificationStatus status) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setUser(createTestUser());
        notification.setTitle("Test Notification");
        notification.setMessage("Test message");
        notification.setType(NotificationType.SYSTEM);
        notification.setStatus(status);
        notification.setCreatedAt(LocalDateTime.now());
        return notification;
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetNotifications() throws Exception {
        // Given
        Notification notification = createTestNotification(1L, NotificationStatus.UNREAD);
        List<Notification> notifications = Arrays.asList(notification);

        when(notificationService.getUserNotifications(1L)).thenReturn(notifications);

        // When & Then
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Notification"))
                .andExpect(jsonPath("$[0].status").value("UNREAD"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetUnreadNotifications() throws Exception {
        // Given
        Notification notification = createTestNotification(1L, NotificationStatus.UNREAD);
        List<Notification> notifications = Arrays.asList(notification);

        when(notificationService.getUnreadNotifications(1L)).thenReturn(notifications);

        // When & Then
        mockMvc.perform(get("/notifications/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("UNREAD"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetUnreadCount() throws Exception {
        // Given
        when(notificationService.getUnreadCount(1L)).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/notifications/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testMarkAsRead() throws Exception {
        // Given
        doNothing().when(notificationService).markAsRead(1L);

        // When & Then
        mockMvc.perform(post("/notifications/1/read")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(notificationService).markAsRead(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testMarkAllAsRead() throws Exception {
        // Given
        doNothing().when(notificationService).markAllAsRead(1L);

        // When & Then
        mockMvc.perform(post("/notifications/read-all")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(notificationService).markAllAsRead(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testDeleteNotification() throws Exception {
        // Given
        doNothing().when(notificationService).deleteNotification(1L);

        // When & Then
        mockMvc.perform(delete("/notifications/1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(notificationService).deleteNotification(1L);
    }
}

