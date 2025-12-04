package com.habittracker.repository;

import com.habittracker.model.ChallengeParticipation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeParticipationRepository extends JpaRepository<ChallengeParticipation, Long> {

    // Find participation by challenge and user
    Optional<ChallengeParticipation> findByChallengeIdAndUserId(Long challengeId, Long userId);

    // Check if user is already participating
    boolean existsByChallengeIdAndUserId(Long challengeId, Long userId);

    // Get leaderboard for a challenge (sorted by completion count)
    @Query("SELECT p FROM ChallengeParticipation p WHERE p.challenge.id = :challengeId ORDER BY p.completionCount DESC")
    List<ChallengeParticipation> findLeaderboardByChallengeId(@Param("challengeId") Long challengeId);

    // Get paginated leaderboard
    @Query("SELECT p FROM ChallengeParticipation p WHERE p.challenge.id = :challengeId ORDER BY p.completionCount DESC")
    Page<ChallengeParticipation> findLeaderboardByChallengeId(@Param("challengeId") Long challengeId,
            Pageable pageable);

    // Get team leaderboard (aggregated by team)
    @Query("SELECT p.teamName, SUM(p.completionCount) as total FROM ChallengeParticipation p " +
            "WHERE p.challenge.id = :challengeId AND p.teamName IS NOT NULL " +
            "GROUP BY p.teamName ORDER BY total DESC")
    List<Object[]> findTeamLeaderboardByChallengeId(@Param("challengeId") Long challengeId);

    // Count participants in a challenge
    Long countByChallengeId(Long challengeId);

    // Get user's rank in challenge
    @Query("SELECT COUNT(p) + 1 FROM ChallengeParticipation p " +
            "WHERE p.challenge.id = :challengeId AND p.completionCount > " +
            "(SELECT p2.completionCount FROM ChallengeParticipation p2 WHERE p2.challenge.id = :challengeId AND p2.user.id = :userId)")
    Integer findUserRankInChallenge(@Param("challengeId") Long challengeId, @Param("userId") Long userId);
}
