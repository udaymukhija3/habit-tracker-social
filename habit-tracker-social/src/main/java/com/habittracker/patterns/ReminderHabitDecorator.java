package com.habittracker.patterns;

import com.habittracker.model.Habit;

public class ReminderHabitDecorator extends HabitDecorator {
    
    private String reminderTime;
    
    public ReminderHabitDecorator(Habit habit, String reminderTime) {
        super(habit);
        this.reminderTime = reminderTime;
    }
    
    @Override
    public String getEnhancedDescription() {
        return habit.getDescription() + " [Reminder: " + reminderTime + "]";
    }
    
    public String getReminderTime() {
        return reminderTime;
    }
    
    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }
}
