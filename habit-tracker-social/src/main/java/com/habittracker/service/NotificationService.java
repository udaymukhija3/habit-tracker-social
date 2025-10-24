package com.habittracker.service;

import com.habittracker.model.Notification;
import com.habittracker.model.NotificationStatus;
import com.habittracker.model.NotificationType;
import com.habittracker.model.User;
import com.habittracker.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    public Notification createNotification(User user, String title, String message, NotificationType type) {
        Notification notification = new Notification(user, title, message, type);
        return notificationRepository.save(notification);
    }
    
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findUnreadNotificationsByUserId(userId);
    }
    
    public List<Notification> getNotificationsByStatus(Long userId, NotificationStatus status) {
        return notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
    }
    
    public List<Notification> getNotificationsByType(Long userId, NotificationType type) {
        return notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
    }
    
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        });
    }
    
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        for (Notification notification : unreadNotifications) {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
        }
        notificationRepository.saveAll(unreadNotifications);
    }
    
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
    
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadNotificationsByUserId(userId);
    }
    
    public void createStreakMilestoneNotification(User user, String habitName, Integer streakCount) {
        String title = "Streak Milestone! 🔥";
        String message = String.format("Congratulations! You've reached a %d-day streak for '%s'!", streakCount, habitName);
        createNotification(user, title, message, NotificationType.STREAK_MILESTONE);
    }
    
    public void createFriendRequestNotification(User user, String requesterName) {
        String title = "New Friend Request";
        String message = String.format("%s wants to be your friend!", requesterName);
        createNotification(user, title, message, NotificationType.FRIEND_REQUEST);
    }
    
    public void createFriendAchievementNotification(User user, String friendName, String achievement) {
        String title = "Friend Achievement! 🎉";
        String message = String.format("%s just achieved: %s", friendName, achievement);
        createNotification(user, title, message, NotificationType.FRIEND_ACHIEVEMENT);
    }
    
    public void createReminderNotification(User user, String habitName) {
        String title = "Habit Reminder";
        String message = String.format("Don't forget to complete your habit: %s", habitName);
        createNotification(user, title, message, NotificationType.REMINDER);
    }
    
    public void createMotivationalNotification(User user, String message) {
        String title = "Motivational Message 💪";
        createNotification(user, title, message, NotificationType.MOTIVATIONAL);
    }
}
