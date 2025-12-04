package com.habittracker.service;

import com.habittracker.model.Notification;
import com.habittracker.model.NotificationStatus;
import com.habittracker.model.NotificationType;
import com.habittracker.model.User;
import com.habittracker.repository.NotificationRepository;
import com.habittracker.websocket.NotificationWebSocketHandler;
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

    @Autowired
    private NotificationWebSocketHandler webSocketHandler;

    public Notification createNotification(User user, String title, String message, NotificationType type) {
        Notification notification = new Notification(user, title, message, type);
        Notification savedNotification = notificationRepository.save(notification);

        // Send real-time notification via WebSocket
        webSocketHandler.sendNotificationToUser(user.getId(), savedNotification);

        return savedNotification;
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
        String message = String.format("Congratulations! You've reached a %d-day streak for '%s'!", streakCount,
                habitName);
        Notification notification = createNotification(user, title, message, NotificationType.STREAK_MILESTONE);

        // Send special streak milestone via WebSocket
        webSocketHandler.sendStreakMilestone(user.getId(), notification);
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

    // ========== Social Proof Notifications ==========

    /**
     * Notify user when a friend completes a habit
     */
    public void createFriendCompletedHabitNotification(User user, String friendName, String habitName) {
        String title = "Friend Activity! 🎯";
        String message = String.format("%s just completed '%s'!", friendName, habitName);
        createNotification(user, title, message, NotificationType.FRIEND_COMPLETED_HABIT);
    }

    /**
     * Daily summary of friend activities (social proof)
     */
    public void createFriendsActiveTodayNotification(User user, int friendCount) {
        String title = "Friends Are Active! 🔥";
        String message = String.format("%d of your friends completed habits today. Don't get left behind!",
                friendCount);
        createNotification(user, title, message, NotificationType.FRIENDS_ACTIVE_TODAY);
    }

    /**
     * Notify when a friend reaches a streak milestone
     */
    public void createFriendStreakMilestoneNotification(User user, String friendName, String habitName,
            Integer streakCount) {
        String title = "Friend Milestone! 🏆";
        String message = String.format("%s reached a %d-day streak for '%s'! Send them encouragement!",
                friendName, streakCount, habitName);
        createNotification(user, title, message, NotificationType.FRIEND_STREAK_MILESTONE);
    }

    /**
     * Notify when accountability partner needs encouragement
     */
    public void createPartnerNeedsEncouragementNotification(User user, String partnerName) {
        String title = "Support Your Partner 🤝";
        String message = String.format("%s hasn't completed their habits today. Send them a message!", partnerName);
        createNotification(user, title, message, NotificationType.PARTNER_NEEDS_ENCOURAGEMENT);
    }
}
