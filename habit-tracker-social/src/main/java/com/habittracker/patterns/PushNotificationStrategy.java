package com.habittracker.patterns;

import com.habittracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationStrategy implements NotificationStrategy {
    
    @Override
    public void sendNotification(User user, String title, String message) {
        // In a real implementation, this would send a push notification
        System.out.println("Sending push notification to " + user.getUsername() + ": " + title + " - " + message);
    }
    
    @Override
    public boolean canHandle(String notificationType) {
        return "PUSH".equalsIgnoreCase(notificationType);
    }
}
