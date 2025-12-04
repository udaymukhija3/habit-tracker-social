package com.habittracker.repository;

import com.habittracker.model.ActivityFeed;
import com.habittracker.model.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, Long> {

    // Get activities from a specific user
    Page<ActivityFeed> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Get activities from user's friends (requires friend IDs)
    @Query("SELECT a FROM ActivityFeed a WHERE a.user.id IN :friendIds ORDER BY a.createdAt DESC")
    Page<ActivityFeed> findByFriendIdsOrderByCreatedAtDesc(@Param("friendIds") List<Long> friendIds, Pageable pageable);

    // Get activities by type
    Page<ActivityFeed> findByActivityTypeOrderByCreatedAtDesc(ActivityType activityType, Pageable pageable);

    // Get activities from friends filtered by type
    @Query("SELECT a FROM ActivityFeed a WHERE a.user.id IN :friendIds AND a.activityType = :activityType ORDER BY a.createdAt DESC")
    Page<ActivityFeed> findByFriendIdsAndActivityTypeOrderByCreatedAtDesc(
            @Param("friendIds") List<Long> friendIds,
            @Param("activityType") ActivityType activityType,
            Pageable pageable);

    // Get activities within date range
    @Query("SELECT a FROM ActivityFeed a WHERE a.user.id IN :friendIds AND a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    Page<ActivityFeed> findByFriendIdsAndDateRangeOrderByCreatedAtDesc(
            @Param("friendIds") List<Long> friendIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // Count activities for a user
    Long countByUserId(Long userId);

    // Count activities from friends today
    @Query("SELECT COUNT(a) FROM ActivityFeed a WHERE a.user.id IN :friendIds AND a.createdAt >= :startOfDay")
    Long countFriendActivitiesToday(@Param("friendIds") List<Long> friendIds,
            @Param("startOfDay") LocalDateTime startOfDay);
}
