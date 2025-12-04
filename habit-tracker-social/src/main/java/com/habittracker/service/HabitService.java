package com.habittracker.service;

import com.habittracker.model.Habit;
import com.habittracker.model.HabitCompletion;
import com.habittracker.model.HabitType;
import com.habittracker.repository.HabitRepository;
import com.habittracker.repository.HabitCompletionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HabitService {

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private HabitCompletionRepository habitCompletionRepository;

    @Autowired
    private StreakService streakService;

    public Habit createHabit(Habit habit) {
        Habit savedHabit = habitRepository.save(habit);
        // Initialize streak for the habit
        streakService.initializeStreak(savedHabit);
        return savedHabit;
    }

    @Cacheable(value = "habits", key = "#id")
    public Optional<Habit> findById(Long id) {
        return habitRepository.findById(id);
    }

    public List<Habit> findByUserId(Long userId) {
        return habitRepository.findByUserId(userId);
    }

    @Cacheable(value = "habits", key = "'user:' + #userId")
    public List<Habit> findActiveHabitsByUserId(Long userId) {
        return habitRepository.findByUserIdAndIsActiveTrue(userId);
    }

    // Paginated version
    public Page<Habit> findActiveHabitsByUserId(Long userId, Pageable pageable) {
        return habitRepository.findByUserIdAndIsActiveTrue(userId, pageable);
    }

    public List<Habit> findByType(HabitType type) {
        return habitRepository.findByTypeAndIsActiveTrue(type);
    }

    @CacheEvict(value = "habits", key = "#habit.id")
    public Habit updateHabit(Habit habit) {
        return habitRepository.save(habit);
    }

    @CacheEvict(value = "habits", key = "#id")
    public void deleteHabit(Long id) {
        habitRepository.deleteById(id);
    }

    public HabitCompletion completeHabit(Long habitId, Integer completionValue, String notes) {
        Optional<Habit> habitOpt = habitRepository.findById(habitId);
        if (habitOpt.isEmpty()) {
            throw new RuntimeException("Habit not found");
        }

        Habit habit = habitOpt.get();
        HabitCompletion completion = new HabitCompletion(habit, LocalDateTime.now(), completionValue);
        completion.setNotes(notes);

        HabitCompletion savedCompletion = habitCompletionRepository.save(completion);

        // Update streak
        streakService.updateStreak(habit);

        return savedCompletion;
    }

    public List<HabitCompletion> getHabitCompletions(Long habitId) {
        return habitCompletionRepository.findByHabitIdOrderByCompletionDateDesc(habitId);
    }

    // Paginated version
    public Page<HabitCompletion> getHabitCompletions(Long habitId, Pageable pageable) {
        return habitCompletionRepository.findByHabitIdOrderByCompletionDateDesc(habitId, pageable);
    }

    public List<HabitCompletion> getHabitCompletionsByDateRange(Long habitId, LocalDateTime startDate,
            LocalDateTime endDate) {
        return habitCompletionRepository.findByHabitIdAndDateRange(habitId, startDate, endDate);
    }

    public Long getCompletionCount(Long habitId, LocalDateTime startDate, LocalDateTime endDate) {
        return habitCompletionRepository.countCompletionsByHabitIdAndDateRange(habitId, startDate, endDate);
    }

    public void deactivateHabit(Long habitId) {
        habitRepository.findById(habitId).ifPresent(habit -> {
            habit.setActive(false);
            habitRepository.save(habit);
        });
    }

    public Long getActiveHabitCount(Long userId) {
        return habitRepository.countActiveHabitsByUserId(userId);
    }
}
