package com.habittracker.repository;

import com.habittracker.model.Streak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StreakRepository extends JpaRepository<Streak, Long> {
    
    Optional<Streak> findByHabitIdAndUserId(Long habitId, Long userId);
    
    List<Streak> findByUserIdOrderByCurrentStreakDesc(Long userId);
    
    List<Streak> findByHabitIdOrderByCurrentStreakDesc(Long habitId);
    
    @Query("SELECT s FROM Streak s WHERE s.user.id = :userId ORDER BY s.currentStreak DESC")
    List<Streak> findTopStreaksByUserId(@Param("userId") Long userId);
    
    @Query("SELECT s FROM Streak s WHERE s.habit.id = :habitId ORDER BY s.currentStreak DESC")
    List<Streak> findTopStreaksByHabitId(@Param("habitId") Long habitId);
    
    @Query("SELECT s FROM Streak s WHERE s.currentStreak >= :minStreak ORDER BY s.currentStreak DESC")
    List<Streak> findStreaksByMinValue(@Param("minStreak") Integer minStreak);
}
