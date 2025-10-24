package com.habittracker.repository;

import com.habittracker.model.CompetitionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionParticipantRepository extends JpaRepository<CompetitionParticipant, Long> {
    
    List<CompetitionParticipant> findByCompetitionIdOrderByScoreDesc(Long competitionId);
    
    List<CompetitionParticipant> findByUserId(Long userId);
    
    Optional<CompetitionParticipant> findByCompetitionIdAndUserId(Long competitionId, Long userId);
    
    @Query("SELECT cp FROM CompetitionParticipant cp WHERE cp.competition.id = :competitionId ORDER BY cp.score DESC")
    List<CompetitionParticipant> findLeaderboardByCompetitionId(@Param("competitionId") Long competitionId);
    
    @Query("SELECT cp FROM CompetitionParticipant cp WHERE cp.user.id = :userId AND cp.competition.isActive = true")
    List<CompetitionParticipant> findActiveCompetitionsByUserId(@Param("userId") Long userId);
    
    boolean existsByCompetitionIdAndUserId(Long competitionId, Long userId);
}
