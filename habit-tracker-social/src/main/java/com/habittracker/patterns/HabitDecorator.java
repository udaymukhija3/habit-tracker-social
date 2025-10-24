package com.habittracker.patterns;

import com.habittracker.model.Habit;

public abstract class HabitDecorator extends Habit {
    protected Habit habit;
    
    public HabitDecorator(Habit habit) {
        this.habit = habit;
    }
    
    @Override
    public String getName() {
        return habit.getName();
    }
    
    @Override
    public void setName(String name) {
        habit.setName(name);
    }
    
    @Override
    public String getDescription() {
        return habit.getDescription();
    }
    
    @Override
    public void setDescription(String description) {
        habit.setDescription(description);
    }
    
    // Abstract method to be implemented by concrete decorators
    public abstract String getEnhancedDescription();
}
