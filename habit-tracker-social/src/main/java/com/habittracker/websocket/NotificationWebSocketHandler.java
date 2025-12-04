package com.habittracker.websocket;

import com.habittracker.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * WebSocket Notification Handler
 * 
 * Sends real-time notifications to connected clients via WebSocket
 */
@Component
public class NotificationWebSocketHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Send notification to specific user
     * 
     * @param userId       User ID to send notification to
     * @param notification Notification object
     */
    public void sendNotificationToUser(Long userId, Notification notification) {
        try {
            // Send to user-specific queue
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    notification);
        } catch (Exception e) {
            // Log error but don't fail the notification creation
            System.err.println("Failed to send WebSocket notification to user " + userId + ": " + e.getMessage());
        }
    }

    /**
     * Send notification to all users (broadcast)
     * 
     * @param notification Notification object
     */
    public void broadcastNotification(Notification notification) {
        try {
            messagingTemplate.convertAndSend("/topic/notifications", notification);
        } catch (Exception e) {
            System.err.println("Failed to broadcast WebSocket notification: " + e.getMessage());
        }
    }

    /**
     * Send friend activity update
     * 
     * @param userId       User ID to notify
     * @param activityData Activity data (friend completed habit, etc.)
     */
    public void sendFriendActivity(Long userId, Object activityData) {
        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/friend-activity",
                    activityData);
        } catch (Exception e) {
            System.err.println("Failed to send friend activity to user " + userId + ": " + e.getMessage());
        }
    }

    /**
     * Send streak milestone celebration
     * 
     * @param userId     User ID to notify
     * @param streakData Streak milestone data
     */
    public void sendStreakMilestone(Long userId, Object streakData) {
        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/streak-milestone",
                    streakData);
        } catch (Exception e) {
            System.err.println("Failed to send streak milestone to user " + userId + ": " + e.getMessage());
        }
    }
}
