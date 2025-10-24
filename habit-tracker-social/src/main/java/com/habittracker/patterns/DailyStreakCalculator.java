package com.habittracker.patterns;

import com.habittracker.model.Habit;
import com.habittracker.model.HabitCompletion;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class DailyStreakCalculator extends StreakCalculator {
    
    @Override
    protected int calculateSpecificStreak(Habit habit, List<HabitCompletion> completions) {
        int streak = 0;
        LocalDate currentDate = LocalDate.now();
        
        for (HabitCompletion completion : completions) {
            LocalDate completionDate = completion.getCompletionDate().toLocalDate();
            long daysBetween = ChronoUnit.DAYS.between(completionDate, currentDate);
            
            if (daysBetween == streak) {
                streak++;
                currentDate = completionDate;
            } else if (daysBetween > streak) {
                break; // Streak broken
            }
        }
        
        return streak;
    }
    
    @Override
    protected boolean isValidCompletion(java.time.LocalDateTime completionDate) {
        return ChronoUnit.DAYS.between(completionDate.toLocalDate(), java.time.LocalDate.now()) <= 1;
    }
}
