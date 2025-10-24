package com.habittracker.patterns;

import com.habittracker.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationContext {
    
    @Autowired
    private List<NotificationStrategy> strategies;
    
    public void sendNotification(User user, String title, String message, String notificationType) {
        NotificationStrategy strategy = strategies.stream()
                .filter(s -> s.canHandle(notificationType))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No notification strategy found for type: " + notificationType));
        
        strategy.sendNotification(user, title, message);
    }
}
