package com.habittracker.repository;

import com.habittracker.model.Competition;
import com.habittracker.model.CompetitionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    
    List<Competition> findByCreatorIdAndIsActiveTrue(Long creatorId);
    
    List<Competition> findByIsActiveTrue();
    
    List<Competition> findByTypeAndIsActiveTrue(CompetitionType type);
    
    @Query("SELECT c FROM Competition c WHERE c.isActive = true AND c.startDate <= :now AND c.endDate >= :now")
    List<Competition> findActiveCompetitionsByDate(@Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Competition c WHERE c.isActive = true AND c.endDate < :now")
    List<Competition> findExpiredCompetitions(@Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Competition c WHERE c.habit.id = :habitId AND c.isActive = true")
    List<Competition> findActiveCompetitionsByHabitId(@Param("habitId") Long habitId);
}
