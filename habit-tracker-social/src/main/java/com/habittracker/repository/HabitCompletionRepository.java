package com.habittracker.repository;

import com.habittracker.model.HabitCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
    
    List<HabitCompletion> findByHabitIdOrderByCompletionDateDesc(Long habitId);
    
    @Query("SELECT hc FROM HabitCompletion hc WHERE hc.habit.id = :habitId AND hc.completionDate >= :startDate AND hc.completionDate <= :endDate ORDER BY hc.completionDate DESC")
    List<HabitCompletion> findByHabitIdAndDateRange(@Param("habitId") Long habitId, 
                                                   @Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(hc) FROM HabitCompletion hc WHERE hc.habit.id = :habitId AND hc.completionDate >= :startDate AND hc.completionDate <= :endDate")
    Long countCompletionsByHabitIdAndDateRange(@Param("habitId") Long habitId, 
                                              @Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT hc FROM HabitCompletion hc WHERE hc.habit.user.id = :userId AND hc.completionDate >= :startDate AND hc.completionDate <= :endDate ORDER BY hc.completionDate DESC")
    List<HabitCompletion> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                                  @Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
}
