package com.habittracker.patterns;

import com.habittracker.model.Habit;

public class RewardHabitDecorator extends HabitDecorator {
    
    private String reward;
    
    public RewardHabitDecorator(Habit habit, String reward) {
        super(habit);
        this.reward = reward;
    }
    
    @Override
    public String getEnhancedDescription() {
        return habit.getDescription() + " [Reward: " + reward + "]";
    }
    
    public String getReward() {
        return reward;
    }
    
    public void setReward(String reward) {
        this.reward = reward;
    }
}
