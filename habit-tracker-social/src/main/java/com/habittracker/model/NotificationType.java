package com.habittracker.model;

public enum NotificationType {
    STREAK_MILESTONE("Streak Milestone"),
    FRIEND_REQUEST("Friend Request"),
    FRIEND_ACHIEVEMENT("Friend Achievement"),
    REMINDER("Reminder"),
    MOTIVATIONAL("Motivational"),
    SYSTEM("System"),
    FRIEND_COMPLETED_HABIT("Friend Completed Habit"),
    FRIENDS_ACTIVE_TODAY("Friends Active Today"),
    FRIEND_STREAK_MILESTONE("Friend Streak Milestone"),
    PARTNER_NEEDS_ENCOURAGEMENT("Partner Needs Encouragement"),
    CHALLENGE_INVITATION("Challenge Invitation"),
    CHALLENGE_UPDATE("Challenge Update");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
