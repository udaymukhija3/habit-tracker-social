package com.habittracker.model;

public enum HabitFrequency {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    CUSTOM("Custom");
    
    private final String displayName;
    
    HabitFrequency(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
