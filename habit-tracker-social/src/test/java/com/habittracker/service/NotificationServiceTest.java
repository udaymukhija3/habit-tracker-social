package com.habittracker.service;

import com.habittracker.model.Notification;
import com.habittracker.model.NotificationStatus;
import com.habittracker.model.NotificationType;
import com.habittracker.model.Role;
import com.habittracker.model.User;
import com.habittracker.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.USER);

        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUser(testUser);
        testNotification.setTitle("Test Notification");
        testNotification.setMessage("Test message");
        testNotification.setType(NotificationType.SYSTEM);
        testNotification.setStatus(NotificationStatus.UNREAD);
        testNotification.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateNotification() {
        // Given
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // When
        Notification result = notificationService.createNotification(
                testUser, "Test Notification", "Test message", NotificationType.SYSTEM);

        // Then
        assertNotNull(result);
        assertEquals("Test Notification", result.getTitle());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testGetUserNotifications() {
        // Given
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(notifications);

        // When
        List<Notification> result = notificationService.getUserNotifications(1L);

        // Then
        assertEquals(1, result.size());
        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void testGetUnreadNotifications() {
        // Given
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationRepository.findUnreadNotificationsByUserId(1L)).thenReturn(notifications);

        // When
        List<Notification> result = notificationService.getUnreadNotifications(1L);

        // Then
        assertEquals(1, result.size());
        verify(notificationRepository).findUnreadNotificationsByUserId(1L);
    }

    @Test
    void testMarkAsRead() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(java.util.Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // When
        notificationService.markAsRead(1L);

        // Then
        verify(notificationRepository).findById(1L);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testMarkAllAsRead() {
        // Given
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationRepository.findUnreadNotificationsByUserId(1L)).thenReturn(notifications);
        when(notificationRepository.saveAll(anyList())).thenReturn(notifications);

        // When
        notificationService.markAllAsRead(1L);

        // Then
        verify(notificationRepository).findUnreadNotificationsByUserId(1L);
        verify(notificationRepository).saveAll(anyList());
    }

    @Test
    void testDeleteNotification() {
        // When
        notificationService.deleteNotification(1L);

        // Then
        verify(notificationRepository).deleteById(1L);
    }

    @Test
    void testGetUnreadCount() {
        // Given
        when(notificationRepository.countUnreadNotificationsByUserId(1L)).thenReturn(5L);

        // When
        Long count = notificationService.getUnreadCount(1L);

        // Then
        assertEquals(5L, count);
        verify(notificationRepository).countUnreadNotificationsByUserId(1L);
    }

    @Test
    void testCreateStreakMilestoneNotification() {
        // Given
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // When
        notificationService.createStreakMilestoneNotification(testUser, "Test Habit", 7);

        // Then
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testCreateFriendRequestNotification() {
        // Given
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // When
        notificationService.createFriendRequestNotification(testUser, "requester");

        // Then
        verify(notificationRepository).save(any(Notification.class));
    }
}

