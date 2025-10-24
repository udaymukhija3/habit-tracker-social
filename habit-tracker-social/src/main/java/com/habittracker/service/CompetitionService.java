package com.habittracker.service;

import com.habittracker.model.*;
import com.habittracker.repository.CompetitionRepository;
import com.habittracker.repository.CompetitionParticipantRepository;
import com.habittracker.repository.HabitRepository;
import com.habittracker.repository.StreakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CompetitionService {
    
    @Autowired
    private CompetitionRepository competitionRepository;
    
    @Autowired
    private CompetitionParticipantRepository participantRepository;
    
    @Autowired
    private HabitRepository habitRepository;
    
    @Autowired
    private StreakRepository streakRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public Competition createCompetition(Competition competition) {
        return competitionRepository.save(competition);
    }
    
    public CompetitionParticipant joinCompetition(Long competitionId, Long userId) {
        Optional<Competition> competitionOpt = competitionRepository.findById(competitionId);
        if (competitionOpt.isEmpty()) {
            throw new RuntimeException("Competition not found");
        }
        
        Competition competition = competitionOpt.get();
        
        // Check if user is already participating
        if (participantRepository.existsByCompetitionIdAndUserId(competitionId, userId)) {
            throw new RuntimeException("User is already participating in this competition");
        }
        
        // Check if competition is still active
        if (!competition.isActive() || competition.getEndDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Competition is not active or has ended");
        }
        
        User user = new User();
        user.setId(userId);
        
        CompetitionParticipant participant = new CompetitionParticipant(competition, user);
        return participantRepository.save(participant);
    }
    
    public void leaveCompetition(Long competitionId, Long userId) {
        Optional<CompetitionParticipant> participantOpt = participantRepository.findByCompetitionIdAndUserId(competitionId, userId);
        if (participantOpt.isEmpty()) {
            throw new RuntimeException("User is not participating in this competition");
        }
        
        participantRepository.delete(participantOpt.get());
    }
    
    public List<Competition> getActiveCompetitions() {
        return competitionRepository.findActiveCompetitionsByDate(LocalDateTime.now());
    }
    
    public List<Competition> getUserCompetitions(Long userId) {
        return competitionRepository.findByCreatorIdAndIsActiveTrue(userId);
    }
    
    public List<CompetitionParticipant> getCompetitionLeaderboard(Long competitionId) {
        return participantRepository.findLeaderboardByCompetitionId(competitionId);
    }
    
    public List<CompetitionParticipant> getUserActiveCompetitions(Long userId) {
        return participantRepository.findActiveCompetitionsByUserId(userId);
    }
    
    public void updateCompetitionScores(Long competitionId) {
        Optional<Competition> competitionOpt = competitionRepository.findById(competitionId);
        if (competitionOpt.isEmpty()) {
            return;
        }
        
        Competition competition = competitionOpt.get();
        List<CompetitionParticipant> participants = participantRepository.findByCompetitionIdOrderByScoreDesc(competitionId);
        
        for (CompetitionParticipant participant : participants) {
            int score = calculateScore(competition, participant.getUser().getId());
            participant.setScore(score);
        }
        
        // Sort by score and update ranks
        participants.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
        
        for (int i = 0; i < participants.size(); i++) {
            participants.get(i).setRank(i + 1);
        }
        
        participantRepository.saveAll(participants);
    }
    
    private int calculateScore(Competition competition, Long userId) {
        switch (competition.getType()) {
            case STREAK:
                return calculateStreakScore(competition.getHabit().getId(), userId);
            case COMPLETION_COUNT:
                return calculateCompletionScore(competition.getHabit().getId(), userId);
            case CONSISTENCY:
                return calculateConsistencyScore(competition.getHabit().getId(), userId);
            default:
                return 0;
        }
    }
    
    private int calculateStreakScore(Long habitId, Long userId) {
        Optional<Streak> streakOpt = streakRepository.findByHabitIdAndUserId(habitId, userId);
        return streakOpt.map(Streak::getCurrentStreak).orElse(0);
    }
    
    private int calculateCompletionScore(Long habitId, Long userId) {
        // This would need to be implemented based on habit completions
        // For now, returning a placeholder
        return 0;
    }
    
    private int calculateConsistencyScore(Long habitId, Long userId) {
        // This would need to be implemented based on consistency metrics
        // For now, returning a placeholder
        return 0;
    }
    
    public void endCompetition(Long competitionId) {
        Optional<Competition> competitionOpt = competitionRepository.findById(competitionId);
        if (competitionOpt.isEmpty()) {
            return;
        }
        
        Competition competition = competitionOpt.get();
        competition.setActive(false);
        competitionRepository.save(competition);
        
        // Notify participants about competition end
        List<CompetitionParticipant> participants = participantRepository.findByCompetitionIdOrderByScoreDesc(competitionId);
        for (CompetitionParticipant participant : participants) {
            String message = String.format("Competition '%s' has ended! You ranked #%d", 
                                         competition.getName(), participant.getRank());
            notificationService.createNotification(participant.getUser(), "Competition Ended", message, NotificationType.SYSTEM);
        }
    }
}
