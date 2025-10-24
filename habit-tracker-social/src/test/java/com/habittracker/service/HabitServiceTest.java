package com.habittracker.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.habittracker.model.Habit;
import com.habittracker.model.HabitFrequency;
import com.habittracker.model.HabitType;
import com.habittracker.model.User;
import com.habittracker.repository.HabitCompletionRepository;
import com.habittracker.repository.HabitRepository;

@ExtendWith(MockitoExtension.class)
public class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitCompletionRepository habitCompletionRepository;

    @Mock
    private StreakService streakService;

    @InjectMocks
    private HabitService habitService;

    private Habit testHabit;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testHabit = new Habit();
        testHabit.setId(1L);
        testHabit.setName("Test Habit");
        testHabit.setDescription("Test Description");
        testHabit.setType(HabitType.HEALTH);
        testHabit.setFrequency(HabitFrequency.DAILY);
        testHabit.setTargetValue(1);
        testHabit.setTargetUnit("times");
        testHabit.setUser(testUser);
    }

    @Test
    void testCreateHabit() {
        // Given
        when(habitRepository.save(any(Habit.class))).thenReturn(testHabit);

        // When
        Habit result = habitService.createHabit(testHabit);

        // Then
        assertNotNull(result);
        assertEquals("Test Habit", result.getName());
        verify(habitRepository).save(testHabit);
        verify(streakService).initializeStreak(testHabit);
    }

    @Test
    void testFindById() {
        // Given
        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));

        // When
        Optional<Habit> result = habitService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Habit", result.get().getName());
    }

    @Test
    void testFindByUserId() {
        // Given
        List<Habit> habits = Arrays.asList(testHabit);
        when(habitRepository.findByUserId(1L)).thenReturn(habits);

        // When
        List<Habit> result = habitService.findByUserId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Habit", result.get(0).getName());
    }

    @Test
    void testFindActiveHabitsByUserId() {
        // Given
        List<Habit> habits = Arrays.asList(testHabit);
        when(habitRepository.findByUserIdAndIsActiveTrue(1L)).thenReturn(habits);

        // When
        List<Habit> result = habitService.findActiveHabitsByUserId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Habit", result.get(0).getName());
    }

    @Test
    void testUpdateHabit() {
        // Given
        when(habitRepository.save(any(Habit.class))).thenReturn(testHabit);

        // When
        Habit result = habitService.updateHabit(testHabit);

        // Then
        assertNotNull(result);
        verify(habitRepository).save(testHabit);
    }

    @Test
    void testDeleteHabit() {
        // When
        habitService.deleteHabit(1L);

        // Then
        verify(habitRepository).deleteById(1L);
    }
}
