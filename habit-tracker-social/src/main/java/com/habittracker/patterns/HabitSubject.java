package com.habittracker.patterns;

import com.habittracker.model.HabitCompletion;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HabitSubject {
    
    private List<HabitObserver> observers = new ArrayList<>();
    
    public void addObserver(HabitObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(HabitObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers(HabitCompletion completion) {
        for (HabitObserver observer : observers) {
            observer.update(completion);
        }
    }
    
    public void notifyStreakUpdate(Long habitId, Integer streakCount) {
        for (HabitObserver observer : observers) {
            observer.updateStreak(habitId, streakCount);
        }
    }
    
    public void notifyAchievement(Long userId, String achievement) {
        for (HabitObserver observer : observers) {
            observer.updateAchievement(userId, achievement);
        }
    }
}
