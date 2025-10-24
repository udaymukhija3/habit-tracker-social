package com.habittracker.patterns;

import com.habittracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationStrategy implements NotificationStrategy {
    
    @Override
    public void sendNotification(User user, String title, String message) {
        // In a real implementation, this would send an email
        System.out.println("Sending email to " + user.getEmail() + ": " + title + " - " + message);
    }
    
    @Override
    public boolean canHandle(String notificationType) {
        return "EMAIL".equalsIgnoreCase(notificationType);
    }
}
