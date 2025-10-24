package com.habittracker.model;

public enum NotificationType {
    STREAK_MILESTONE("Streak Milestone"),
    FRIEND_REQUEST("Friend Request"),
    FRIEND_ACHIEVEMENT("Friend Achievement"),
    REMINDER("Reminder"),
    MOTIVATIONAL("Motivational"),
    SYSTEM("System");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
