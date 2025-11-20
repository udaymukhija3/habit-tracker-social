package com.habittracker.service;

import com.habittracker.model.Competition;
import com.habittracker.model.CompetitionParticipant;
import com.habittracker.model.CompetitionType;
import com.habittracker.model.Habit;
import com.habittracker.model.Role;
import com.habittracker.model.User;
import com.habittracker.repository.CompetitionParticipantRepository;
import com.habittracker.repository.CompetitionRepository;
import com.habittracker.repository.HabitRepository;
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
public class CompetitionServiceTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private CompetitionParticipantRepository participantRepository;

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CompetitionService competitionService;

    private User testUser;
    private Habit testHabit;
    private Competition testCompetition;
    private CompetitionParticipant testParticipant;

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
        testHabit.setUser(testUser);

        testCompetition = new Competition();
        testCompetition.setId(1L);
        testCompetition.setName("Test Competition");
        testCompetition.setType(CompetitionType.STREAK);
        testCompetition.setHabit(testHabit);
        testCompetition.setActive(true);
        testCompetition.setStartDate(LocalDateTime.now().minusDays(1));
        testCompetition.setEndDate(LocalDateTime.now().plusDays(7));

        testParticipant = new CompetitionParticipant(testCompetition, testUser);
        testParticipant.setId(1L);
        testParticipant.setScore(10);
        testParticipant.setRank(1);
    }

    @Test
    void testCreateCompetition() {
        // Given
        when(competitionRepository.save(any(Competition.class))).thenReturn(testCompetition);

        // When
        Competition result = competitionService.createCompetition(testCompetition);

        // Then
        assertNotNull(result);
        assertEquals("Test Competition", result.getName());
        verify(competitionRepository).save(testCompetition);
    }

    @Test
    void testJoinCompetition_Success() {
        // Given
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(testCompetition));
        when(participantRepository.existsByCompetitionIdAndUserId(1L, 1L)).thenReturn(false);
        when(participantRepository.save(any(CompetitionParticipant.class))).thenReturn(testParticipant);

        // When
        CompetitionParticipant result = competitionService.joinCompetition(1L, 1L);

        // Then
        assertNotNull(result);
        verify(participantRepository).save(any(CompetitionParticipant.class));
    }

    @Test
    void testJoinCompetition_NotFound() {
        // Given
        when(competitionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            competitionService.joinCompetition(1L, 1L);
        });
    }

    @Test
    void testJoinCompetition_AlreadyParticipating() {
        // Given
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(testCompetition));
        when(participantRepository.existsByCompetitionIdAndUserId(1L, 1L)).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            competitionService.joinCompetition(1L, 1L);
        });
    }

    @Test
    void testLeaveCompetition_Success() {
        // Given
        when(participantRepository.findByCompetitionIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(testParticipant));
        doNothing().when(participantRepository).delete(testParticipant);

        // When
        competitionService.leaveCompetition(1L, 1L);

        // Then
        verify(participantRepository).delete(testParticipant);
    }

    @Test
    void testLeaveCompetition_NotParticipating() {
        // Given
        when(participantRepository.findByCompetitionIdAndUserId(1L, 1L))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            competitionService.leaveCompetition(1L, 1L);
        });
    }

    @Test
    void testGetActiveCompetitions() {
        // Given
        List<Competition> competitions = Arrays.asList(testCompetition);
        when(competitionRepository.findActiveCompetitionsByDate(any(LocalDateTime.class)))
                .thenReturn(competitions);

        // When
        List<Competition> result = competitionService.getActiveCompetitions();

        // Then
        assertEquals(1, result.size());
        verify(competitionRepository).findActiveCompetitionsByDate(any(LocalDateTime.class));
    }

    @Test
    void testGetUserCompetitions() {
        // Given
        List<Competition> competitions = Arrays.asList(testCompetition);
        when(competitionRepository.findByCreatorIdAndIsActiveTrue(1L)).thenReturn(competitions);

        // When
        List<Competition> result = competitionService.getUserCompetitions(1L);

        // Then
        assertEquals(1, result.size());
        verify(competitionRepository).findByCreatorIdAndIsActiveTrue(1L);
    }

    @Test
    void testGetCompetitionLeaderboard() {
        // Given
        List<CompetitionParticipant> participants = Arrays.asList(testParticipant);
        when(participantRepository.findLeaderboardByCompetitionId(1L)).thenReturn(participants);

        // When
        List<CompetitionParticipant> result = competitionService.getCompetitionLeaderboard(1L);

        // Then
        assertEquals(1, result.size());
        verify(participantRepository).findLeaderboardByCompetitionId(1L);
    }

    @Test
    void testEndCompetition() {
        // Given
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(testCompetition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(testCompetition);
        when(participantRepository.findByCompetitionIdOrderByScoreDesc(1L))
                .thenReturn(Arrays.asList(testParticipant));
        doNothing().when(notificationService).createNotification(any(), anyString(), anyString(), any());

        // When
        competitionService.endCompetition(1L);

        // Then
        assertFalse(testCompetition.isActive());
        verify(competitionRepository).save(testCompetition);
    }
}

