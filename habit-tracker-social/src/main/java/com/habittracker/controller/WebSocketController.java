package com.habittracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.habittracker.model.Notification;
import com.habittracker.model.User;
import com.habittracker.service.NotificationService;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/notifications")
    @SendTo("/topic/notifications")
    public List<Notification> getNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            return notificationService.getUserNotifications(user.getId());
        }
        return List.of();
    }

    public void sendNotificationToUser(Long userId, Notification notification) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(), 
            "/queue/notifications", 
            notification
        );
    }

    public void sendHabitCompletionUpdate(Long userId, String message) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(), 
            "/topic/habit-updates", 
            message
        );
    }

    public void sendStreakUpdate(Long userId, String message) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(), 
            "/topic/streak-updates", 
            message
        );
    }
}
