package com.habittracker.model;

public enum HabitType {
    HEALTH("Health & Fitness"),
    PRODUCTIVITY("Productivity"),
    LEARNING("Learning & Education"),
    SOCIAL("Social & Relationships"),
    FINANCE("Financial"),
    MINDFULNESS("Mindfulness & Wellness"),
    CREATIVE("Creative & Hobbies"),
    MAINTENANCE("Maintenance & Chores");
    
    private final String displayName;
    
    HabitType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
