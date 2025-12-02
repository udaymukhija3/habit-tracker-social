package com.habittracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habittracker.dto.HabitDTO;
import com.habittracker.model.Habit;
import com.habittracker.model.HabitCompletion;
import com.habittracker.model.HabitFrequency;
import com.habittracker.model.HabitType;
import com.habittracker.model.User;
import com.habittracker.service.HabitService;
import com.habittracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(HabitController.class)
public class HabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HabitService habitService;

    @MockBean(name = "userService")
    private UserService userService;

    @MockBean
    private com.habittracker.security.RateLimitService rateLimitService;

    @MockBean
    private com.habittracker.security.JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        return user;
    }

    @BeforeEach
    public void setup() {
        User user = createTestUser();
        when(userService.loadUserByUsername("testuser")).thenReturn(user);
    }

    private Habit createTestHabit() {
        Habit habit = new Habit();
        habit.setId(1L);
        habit.setName("Test Habit");
        habit.setDescription("Test Description");
        habit.setType(HabitType.HEALTH);
        habit.setFrequency(HabitFrequency.DAILY);
        habit.setTargetValue(1);
        habit.setTargetUnit("times");
        habit.setUser(createTestUser());
        return habit;
    }

    @Test
    @WithUserDetails(value = "testuser", userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testGetUserHabits() throws Exception {
        // Given
        Habit habit = createTestHabit();
        List<Habit> habits = Arrays.asList(habit);
        when(habitService.findActiveHabitsByUserId(1L)).thenReturn(habits);

        // When & Then
        mockMvc.perform(get("/habits"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Habit"))
                .andExpect(jsonPath("$[0].type").value("HEALTH"));
    }

    @Test
    @WithUserDetails(value = "testuser", userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testCreateHabit() throws Exception {
        // Given
        HabitDTO habitDTO = new HabitDTO();
        habitDTO.setName("New Habit");
        habitDTO.setDescription("New Description");
        habitDTO.setType(HabitType.PRODUCTIVITY);
        habitDTO.setFrequency(HabitFrequency.DAILY);
        habitDTO.setTargetValue(1);
        habitDTO.setTargetUnit("times");

        Habit createdHabit = createTestHabit();
        createdHabit.setName("New Habit");
        when(habitService.createHabit(any(Habit.class))).thenReturn(createdHabit);

        // When & Then
        mockMvc.perform(post("/habits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(habitDTO))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Habit"));
    }

    @Test
    @WithUserDetails(value = "testuser", userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testGetHabit() throws Exception {
        // Given
        Habit habit = createTestHabit();
        when(habitService.findById(1L)).thenReturn(Optional.of(habit));

        // When & Then
        mockMvc.perform(get("/habits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Habit"));
    }

    @Test
    @WithUserDetails(value = "testuser", userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testGetHabitNotFound() throws Exception {
        // Given
        when(habitService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/habits/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "testuser", userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testUpdateHabit() throws Exception {
        // Given
        Habit habit = createTestHabit();
        HabitDTO habitDTO = new HabitDTO();
        habitDTO.setName("Updated Habit");
        habitDTO.setDescription("Updated Description");
        habitDTO.setType(HabitType.HEALTH);
        habitDTO.setFrequency(HabitFrequency.DAILY);
        habitDTO.setTargetValue(2);
        habitDTO.setTargetUnit("times");

        when(habitService.findById(1L)).thenReturn(Optional.of(habit));
        when(habitService.updateHabit(any(Habit.class))).thenReturn(habit);

        // When & Then
        mockMvc.perform(put("/habits/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(habitDTO))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "testuser", userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testDeleteHabit() throws Exception {
        // Given
        Habit habit = createTestHabit();
        when(habitService.findById(1L)).thenReturn(Optional.of(habit));
        doNothing().when(habitService).deleteHabit(1L);

        // When & Then
        mockMvc.perform(delete("/habits/1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(habitService).deleteHabit(1L);
    }

    @Test
    @WithUserDetails(value = "testuser", userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testCompleteHabit() throws Exception {
        // Given
        Habit habit = createTestHabit();
        HabitCompletion completion = new HabitCompletion();
        completion.setId(1L);
        completion.setHabit(habit);

        when(habitService.findById(1L)).thenReturn(Optional.of(habit));
        when(habitService.completeHabit(eq(1L), any(), any())).thenReturn(completion);

        // When & Then
        mockMvc.perform(post("/habits/1/complete")
                .param("value", "1")
                .param("notes", "Completed successfully")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithUserDetails(value = "testuser", userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testGetHabitCompletions() throws Exception {
        // Given
        Habit habit = createTestHabit();
        HabitCompletion completion = new HabitCompletion();
        completion.setId(1L);
        completion.setHabit(habit);
        List<HabitCompletion> completions = Arrays.asList(completion);

        when(habitService.findById(1L)).thenReturn(Optional.of(habit));
        when(habitService.getHabitCompletions(1L)).thenReturn(completions);

        // When & Then
        mockMvc.perform(get("/habits/1/completions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
