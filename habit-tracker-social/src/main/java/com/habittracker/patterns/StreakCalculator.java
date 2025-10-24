package com.habittracker.patterns;

import com.habittracker.model.Habit;
import com.habittracker.model.HabitCompletion;
import com.habittracker.model.HabitFrequency;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public abstract class StreakCalculator {
    
    // Template method that defines the algorithm structure
    public final int calculateStreak(Habit habit, List<HabitCompletion> completions) {
        if (completions.isEmpty()) {
            return 0;
        }
        
        // Sort completions by date (most recent first)
        completions.sort((c1, c2) -> c2.getCompletionDate().compareTo(c1.getCompletionDate()));
        
        // Check if the most recent completion is within the valid timeframe
        if (!isValidCompletion(completions.get(0).getCompletionDate())) {
            return 0;
        }
        
        // Calculate the streak using the specific algorithm
        return calculateSpecificStreak(habit, completions);
    }
    
    // Abstract method to be implemented by subclasses
    protected abstract int calculateSpecificStreak(Habit habit, List<HabitCompletion> completions);
    
    // Hook method that can be overridden
    protected boolean isValidCompletion(LocalDateTime completionDate) {
        LocalDateTime now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(completionDate.toLocalDate(), now.toLocalDate());
        return daysBetween <= getMaxDaysBetweenCompletions();
    }
    
    // Hook method that can be overridden
    protected int getMaxDaysBetweenCompletions() {
        return 1; // Default: consecutive days
    }
}
