package com.habittracker.service;

import com.habittracker.model.Habit;
import com.habittracker.model.HabitCompletion;
import com.habittracker.model.HabitFrequency;
import com.habittracker.model.HabitType;
import com.habittracker.model.Role;
import com.habittracker.model.Streak;
import com.habittracker.model.User;
import com.habittracker.repository.HabitCompletionRepository;
import com.habittracker.repository.StreakRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StreakServiceTest {

    @Mock
    private StreakRepository streakRepository;

    @Mock
    private HabitCompletionRepository habitCompletionRepository;

    @InjectMocks
    private StreakService streakService;

    private User testUser;
    private Habit testHabit;
    private Streak testStreak;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.USER);

        testHabit = new Habit();
        testHabit.setId(1L);
        testHabit.setName("Test Habit");
        testHabit.setType(HabitType.HEALTH);
        testHabit.setFrequency(HabitFrequency.DAILY);
        testHabit.setUser(testUser);

        testStreak = new Streak(testHabit, testUser);
        testStreak.setId(1L);
        testStreak.setCurrentStreak(5);
        testStreak.setLongestStreak(5);
    }

    @Test
    void testInitializeStreak() {
        // Given
        when(streakRepository.save(any(Streak.class))).thenReturn(testStreak);

        // When
        streakService.initializeStreak(testHabit);

        // Then
        verify(streakRepository).save(any(Streak.class));
    }

    @Test
    void testGetStreak() {
        // Given
        when(streakRepository.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.of(testStreak));

        // When
        Optional<Streak> result = streakService.getStreak(1L, 1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(5, result.get().getCurrentStreak());
    }

    @Test
    void testGetUserStreaks() {
        // Given
        List<Streak> streaks = Arrays.asList(testStreak);
        when(streakRepository.findByUserIdOrderByCurrentStreakDesc(1L)).thenReturn(streaks);

        // When
        List<Streak> result = streakService.getUserStreaks(1L);

        // Then
        assertEquals(1, result.size());
        verify(streakRepository).findByUserIdOrderByCurrentStreakDesc(1L);
    }

    @Test
    void testGetHabitStreaks() {
        // Given
        List<Streak> streaks = Arrays.asList(testStreak);
        when(streakRepository.findByHabitIdOrderByCurrentStreakDesc(1L)).thenReturn(streaks);

        // When
        List<Streak> result = streakService.getHabitStreaks(1L);

        // Then
        assertEquals(1, result.size());
        verify(streakRepository).findByHabitIdOrderByCurrentStreakDesc(1L);
    }

    @Test
    void testUpdateStreak_FirstCompletion() {
        // Given
        HabitCompletion completion = new HabitCompletion();
        completion.setCompletionDate(LocalDateTime.now());
        List<HabitCompletion> completions = Arrays.asList(completion);

        when(streakRepository.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        when(streakRepository.save(any(Streak.class))).thenReturn(testStreak);
        when(habitCompletionRepository.findByHabitIdOrderByCompletionDateDesc(1L)).thenReturn(completions);

        // When
        streakService.updateStreak(testHabit);

        // Then
        verify(streakRepository, atLeastOnce()).save(any(Streak.class));
    }

    @Test
    void testUpdateStreak_ConsecutiveDay() {
        // Given
        testStreak.setLastCompletionDate(LocalDateTime.now().minusDays(1));
        HabitCompletion completion = new HabitCompletion();
        completion.setCompletionDate(LocalDateTime.now());
        List<HabitCompletion> completions = Arrays.asList(completion);

        when(streakRepository.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.of(testStreak));
        when(streakRepository.save(any(Streak.class))).thenReturn(testStreak);
        when(habitCompletionRepository.findByHabitIdOrderByCompletionDateDesc(1L)).thenReturn(completions);

        // When
        streakService.updateStreak(testHabit);

        // Then
        verify(streakRepository).save(any(Streak.class));
    }
}

