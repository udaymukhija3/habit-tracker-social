package com.habittracker.patterns;

import com.habittracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationStrategy implements NotificationStrategy {
    
    @Override
    public void sendNotification(User user, String title, String message) {
        // In a real implementation, this would send an SMS
        System.out.println("Sending SMS to " + user.getUsername() + ": " + title + " - " + message);
    }
    
    @Override
    public boolean canHandle(String notificationType) {
        return "SMS".equalsIgnoreCase(notificationType);
    }
}
