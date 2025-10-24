package com.habittracker.patterns;

import com.habittracker.model.HabitCompletion;
import com.habittracker.service.NotificationService;
import com.habittracker.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StreakObserver implements HabitObserver {
    
    @Autowired
    private NotificationService notificationService;
    
    @Override
    public void update(HabitCompletion completion) {
        // This would be called when a habit is completed
        System.out.println("Streak observer notified of completion: " + completion.getHabit().getName());
    }
    
    @Override
    public void updateStreak(Long habitId, Integer streakCount) {
        // Check for milestone streaks (7, 30, 100 days, etc.)
        if (isMilestone(streakCount)) {
            // This would trigger a notification
            System.out.println("Milestone streak reached: " + streakCount + " days for habit " + habitId);
        }
    }
    
    @Override
    public void updateAchievement(Long userId, String achievement) {
        // This would be called when a user achieves something
        System.out.println("Achievement unlocked for user " + userId + ": " + achievement);
    }
    
    private boolean isMilestone(Integer streakCount) {
        return streakCount == 7 || streakCount == 30 || streakCount == 100 || 
               streakCount == 365 || (streakCount % 50 == 0 && streakCount > 100);
    }
}
