package com.habittracker.patterns;

import com.habittracker.model.Habit;
import com.habittracker.model.HabitCompletion;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class WeeklyStreakCalculator extends StreakCalculator {
    
    @Override
    protected int calculateSpecificStreak(Habit habit, List<HabitCompletion> completions) {
        int streak = 0;
        LocalDate currentWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        
        for (HabitCompletion completion : completions) {
            LocalDate completionDate = completion.getCompletionDate().toLocalDate();
            LocalDate completionWeek = completionDate.with(java.time.DayOfWeek.MONDAY);
            
            long weeksBetween = ChronoUnit.WEEKS.between(completionWeek, currentWeek);
            
            if (weeksBetween == streak) {
                streak++;
                currentWeek = completionWeek;
            } else if (weeksBetween > streak) {
                break; // Streak broken
            }
        }
        
        return streak;
    }
    
    @Override
    protected boolean isValidCompletion(java.time.LocalDateTime completionDate) {
        return ChronoUnit.WEEKS.between(completionDate.toLocalDate(), java.time.LocalDate.now()) <= 1;
    }
    
    @Override
    protected int getMaxDaysBetweenCompletions() {
        return 7; // Allow up to 7 days for weekly habits
    }
}
