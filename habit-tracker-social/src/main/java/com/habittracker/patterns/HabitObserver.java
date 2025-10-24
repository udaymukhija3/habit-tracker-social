package com.habittracker.patterns;

import com.habittracker.model.HabitCompletion;

public interface HabitObserver {
    void update(HabitCompletion completion);
    void updateStreak(Long habitId, Integer streakCount);
    void updateAchievement(Long userId, String achievement);
}
