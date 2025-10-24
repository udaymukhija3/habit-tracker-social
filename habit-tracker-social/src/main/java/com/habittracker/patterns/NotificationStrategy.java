package com.habittracker.patterns;

import com.habittracker.model.Notification;
import com.habittracker.model.User;

public interface NotificationStrategy {
    void sendNotification(User user, String title, String message);
    boolean canHandle(String notificationType);
}
