package com.habittracker.service;

import com.habittracker.model.Habit;
import com.habittracker.model.Streak;
import com.habittracker.model.HabitCompletion;
import com.habittracker.repository.StreakRepository;
import com.habittracker.repository.HabitCompletionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StreakService {
    
    @Autowired
    private StreakRepository streakRepository;
    
    @Autowired
    private HabitCompletionRepository habitCompletionRepository;
    
    public void initializeStreak(Habit habit) {
        Streak streak = new Streak(habit, habit.getUser());
        streakRepository.save(streak);
    }
    
    public void updateStreak(Habit habit) {
        Optional<Streak> streakOpt = streakRepository.findByHabitIdAndUserId(habit.getId(), habit.getUser().getId());
        if (streakOpt.isEmpty()) {
            initializeStreak(habit);
            streakOpt = streakRepository.findByHabitIdAndUserId(habit.getId(), habit.getUser().getId());
        }
        
        Streak streak = streakOpt.get();
        LocalDateTime now = LocalDateTime.now();
        
        // Get the last completion date
        List<HabitCompletion> completions = habitCompletionRepository.findByHabitIdOrderByCompletionDateDesc(habit.getId());
        if (completions.isEmpty()) {
            return;
        }
        
        LocalDateTime lastCompletion = completions.get(0).getCompletionDate();
        
        // Check if this is a consecutive day
        if (streak.getLastCompletionDate() != null) {
            long daysBetween = ChronoUnit.DAYS.between(streak.getLastCompletionDate().toLocalDate(), lastCompletion.toLocalDate());
            
            if (daysBetween == 1) {
                // Consecutive day - increment streak
                streak.setCurrentStreak(streak.getCurrentStreak() + 1);
                if (streak.getCurrentStreak() > streak.getLongestStreak()) {
                    streak.setLongestStreak(streak.getCurrentStreak());
                }
            } else if (daysBetween > 1) {
                // Streak broken - reset to 1
                streak.setCurrentStreak(1);
                streak.setStreakStartDate(lastCompletion);
            }
        } else {
            // First completion
            streak.setCurrentStreak(1);
            streak.setStreakStartDate(lastCompletion);
        }
        
        streak.setLastCompletionDate(lastCompletion);
        streakRepository.save(streak);
    }
    
    public Optional<Streak> getStreak(Long habitId, Long userId) {
        return streakRepository.findByHabitIdAndUserId(habitId, userId);
    }
    
    public List<Streak> getUserStreaks(Long userId) {
        return streakRepository.findByUserIdOrderByCurrentStreakDesc(userId);
    }
    
    public List<Streak> getHabitStreaks(Long habitId) {
        return streakRepository.findByHabitIdOrderByCurrentStreakDesc(habitId);
    }
    
    public List<Streak> getTopStreaksByUser(Long userId) {
        return streakRepository.findTopStreaksByUserId(userId);
    }
    
    public List<Streak> getTopStreaksByHabit(Long habitId) {
        return streakRepository.findTopStreaksByHabitId(habitId);
    }
    
    public List<Streak> getStreaksByMinValue(Integer minStreak) {
        return streakRepository.findStreaksByMinValue(minStreak);
    }
}
