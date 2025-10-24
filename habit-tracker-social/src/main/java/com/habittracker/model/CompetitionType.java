package com.habittracker.model;

public enum CompetitionType {
    STREAK("Longest Streak"),
    COMPLETION_COUNT("Most Completions"),
    CONSISTENCY("Most Consistent"),
    CUSTOM("Custom Goal");
    
    private final String displayName;
    
    CompetitionType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
