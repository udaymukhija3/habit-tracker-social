package com.habittracker.patterns;

import com.habittracker.model.Habit;
import com.habittracker.model.HabitType;
import com.habittracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class HabitFactory {
    
    public Habit createHabit(HabitType type, String name, String description, 
                           String frequency, Integer targetValue, String targetUnit, User user) {
        Habit habit = new Habit();
        habit.setName(name);
        habit.setDescription(description);
        habit.setType(type);
        habit.setFrequency(parseFrequency(frequency));
        habit.setTargetValue(targetValue);
        habit.setTargetUnit(targetUnit);
        habit.setUser(user);
        
        // Apply type-specific configurations
        switch (type) {
            case HEALTH:
                return configureHealthHabit(habit);
            case PRODUCTIVITY:
                return configureProductivityHabit(habit);
            case LEARNING:
                return configureLearningHabit(habit);
            case SOCIAL:
                return configureSocialHabit(habit);
            case FINANCE:
                return configureFinanceHabit(habit);
            case MINDFULNESS:
                return configureMindfulnessHabit(habit);
            case CREATIVE:
                return configureCreativeHabit(habit);
            case MAINTENANCE:
                return configureMaintenanceHabit(habit);
            default:
                return habit;
        }
    }
    
    private com.habittracker.model.HabitFrequency parseFrequency(String frequency) {
        return com.habittracker.model.HabitFrequency.valueOf(frequency.toUpperCase());
    }
    
    private Habit configureHealthHabit(Habit habit) {
        // Health habits might have specific default values
        if (habit.getTargetUnit() == null) {
            habit.setTargetUnit("times");
        }
        return habit;
    }
    
    private Habit configureProductivityHabit(Habit habit) {
        // Productivity habits might have different configurations
        if (habit.getTargetUnit() == null) {
            habit.setTargetUnit("tasks");
        }
        return habit;
    }
    
    private Habit configureLearningHabit(Habit habit) {
        // Learning habits might have study-specific configurations
        if (habit.getTargetUnit() == null) {
            habit.setTargetUnit("minutes");
        }
        return habit;
    }
    
    private Habit configureSocialHabit(Habit habit) {
        // Social habits might have interaction-specific configurations
        if (habit.getTargetUnit() == null) {
            habit.setTargetUnit("interactions");
        }
        return habit;
    }
    
    private Habit configureFinanceHabit(Habit habit) {
        // Financial habits might have money-specific configurations
        if (habit.getTargetUnit() == null) {
            habit.setTargetUnit("dollars");
        }
        return habit;
    }
    
    private Habit configureMindfulnessHabit(Habit habit) {
        // Mindfulness habits might have meditation-specific configurations
        if (habit.getTargetUnit() == null) {
            habit.setTargetUnit("minutes");
        }
        return habit;
    }
    
    private Habit configureCreativeHabit(Habit habit) {
        // Creative habits might have project-specific configurations
        if (habit.getTargetUnit() == null) {
            habit.setTargetUnit("projects");
        }
        return habit;
    }
    
    private Habit configureMaintenanceHabit(Habit habit) {
        // Maintenance habits might have task-specific configurations
        if (habit.getTargetUnit() == null) {
            habit.setTargetUnit("tasks");
        }
        return habit;
    }
}
