package com.habittracker.repository;

import com.habittracker.model.ChallengeStatus;
import com.habittracker.model.GroupChallenge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GroupChallengeRepository extends JpaRepository<GroupChallenge, Long> {

    // Find challenges by creator
    Page<GroupChallenge> findByCreatorIdOrderByCreatedAtDesc(Long creatorId, Pageable pageable);

    // Find challenges by status
    Page<GroupChallenge> findByStatusOrderByStartDateAsc(ChallengeStatus status, Pageable pageable);

    // Find active challenges
    @Query("SELECT gc FROM GroupChallenge gc WHERE gc.status = 'ACTIVE' ORDER BY gc.startDate ASC")
    Page<GroupChallenge> findActiveChallenges(Pageable pageable);

    // Find challenges user is participating in
    @Query("SELECT gc FROM GroupChallenge gc JOIN gc.participants p WHERE p.user.id = :userId ORDER BY gc.startDate DESC")
    Page<GroupChallenge> findByParticipantUserId(@Param("userId") Long userId, Pageable pageable);

    // Find pending challenges that should start today
    @Query("SELECT gc FROM GroupChallenge gc WHERE gc.status = 'PENDING' AND gc.startDate <= :today")
    List<GroupChallenge> findPendingChallengesReadyToStart(@Param("today") LocalDate today);

    // Find active challenges that should end today
    @Query("SELECT gc FROM GroupChallenge gc WHERE gc.status = 'ACTIVE' AND gc.endDate <= :today")
    List<GroupChallenge> findActiveChallengesReadyToEnd(@Param("today") LocalDate today);

    // Count active challenges for a user
    @Query("SELECT COUNT(gc) FROM GroupChallenge gc JOIN gc.participants p WHERE p.user.id = :userId AND gc.status = 'ACTIVE'")
    Long countActiveByParticipantUserId(@Param("userId") Long userId);
}
