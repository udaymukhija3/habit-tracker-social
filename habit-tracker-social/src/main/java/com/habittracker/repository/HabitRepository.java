package com.habittracker.repository;

import com.habittracker.model.Habit;
import com.habittracker.model.HabitType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    List<Habit> findByUserIdAndIsActiveTrue(Long userId);

    // Paginated version
    Page<Habit> findByUserIdAndIsActiveTrue(Long userId, Pageable pageable);

    List<Habit> findByUserId(Long userId);

    List<Habit> findByTypeAndIsActiveTrue(HabitType type);

    @Query("SELECT h FROM Habit h WHERE h.user.id = :userId AND h.isActive = true ORDER BY h.createdAt DESC")
    List<Habit> findActiveHabitsByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT h FROM Habit h WHERE h.user.id = :userId AND h.type = :type AND h.isActive = true")
    List<Habit> findActiveHabitsByUserIdAndType(@Param("userId") Long userId, @Param("type") HabitType type);

    @Query("SELECT COUNT(h) FROM Habit h WHERE h.user.id = :userId AND h.isActive = true")
    Long countActiveHabitsByUserId(@Param("userId") Long userId);
}
