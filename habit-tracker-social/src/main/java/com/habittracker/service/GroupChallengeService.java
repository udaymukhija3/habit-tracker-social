package com.habittracker.service;

import com.habittracker.model.*;
import com.habittracker.repository.ChallengeParticipationRepository;
import com.habittracker.repository.GroupChallengeRepository;
import com.habittracker.websocket.NotificationWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Group Challenge Service
 * 
 * Manages team-based challenges for viral growth and social engagement
 */
@Service
@Transactional
public class GroupChallengeService {

    @Autowired
    private GroupChallengeRepository challengeRepository;

    @Autowired
    private ChallengeParticipationRepository participationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ActivityFeedService activityFeedService;

    @Autowired
    private NotificationWebSocketHandler webSocketHandler;

    /**
     * Create a new challenge
     */
    public GroupChallenge createChallenge(User creator, String name, String description,
            ChallengeType type, LocalDate startDate,
            LocalDate endDate, Integer targetCompletions) {
        GroupChallenge challenge = new GroupChallenge(name, creator, type, startDate, endDate);
        challenge.setDescription(description);
        challenge.setTargetCompletions(targetCompletions);

        // If start date is today or earlier, activate immediately
        if (startDate.isBefore(LocalDate.now()) || startDate.isEqual(LocalDate.now())) {
            challenge.setStatus(ChallengeStatus.ACTIVE);
        }

        GroupChallenge savedChallenge = challengeRepository.save(challenge);

        // Creator automatically joins
        joinChallenge(savedChallenge.getId(), creator, null);

        return savedChallenge;
    }

    /**
     * Join a challenge
     */
    public ChallengeParticipation joinChallenge(Long challengeId, User user, String teamName) {
        Optional<GroupChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isEmpty()) {
            throw new RuntimeException("Challenge not found");
        }

        GroupChallenge challenge = challengeOpt.get();

        // Check if already participating
        if (participationRepository.existsByChallengeIdAndUserId(challengeId, user.getId())) {
            throw new RuntimeException("Already participating in this challenge");
        }

        ChallengeParticipation participation = new ChallengeParticipation(challenge, user, teamName);
        ChallengeParticipation saved = participationRepository.save(participation);

        // Create activity feed entry
        activityFeedService.createActivity(user, ActivityType.CHALLENGE_JOINED,
                user.getUsername() + " joined challenge: " + challenge.getName());

        return saved;
    }

    /**
     * Invite a friend to challenge
     */
    public void inviteFriend(Long challengeId, User inviter, User friend) {
        Optional<GroupChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isEmpty()) {
            throw new RuntimeException("Challenge not found");
        }

        GroupChallenge challenge = challengeOpt.get();

        // Send notification to friend
        String title = "Challenge Invitation! 🎯";
        String message = String.format("%s invited you to join '%s'!",
                inviter.getUsername(), challenge.getName());
        notificationService.createNotification(friend, title, message, NotificationType.CHALLENGE_INVITATION);
    }

    /**
     * Record habit completion for challenge
     */
    public void recordCompletion(Long challengeId, Long userId) {
        Optional<ChallengeParticipation> participationOpt = participationRepository
                .findByChallengeIdAndUserId(challengeId, userId);

        if (participationOpt.isPresent()) {
            ChallengeParticipation participation = participationOpt.get();
            participation.incrementCompletion();
            participationRepository.save(participation);

            // Update ranks for all participants
            updateRanks(challengeId);
        }
    }

    /**
     * Get challenge by ID
     */
    public Optional<GroupChallenge> findById(Long id) {
        return challengeRepository.findById(id);
    }

    /**
     * Get active challenges (paginated)
     */
    public Page<GroupChallenge> getActiveChallenges(Pageable pageable) {
        return challengeRepository.findActiveChallenges(pageable);
    }

    /**
     * Get challenges user is participating in
     */
    public Page<GroupChallenge> getUserChallenges(Long userId, Pageable pageable) {
        return challengeRepository.findByParticipantUserId(userId, pageable);
    }

    /**
     * Get challenges created by user
     */
    public Page<GroupChallenge> getChallengesCreatedByUser(Long userId, Pageable pageable) {
        return challengeRepository.findByCreatorIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Get challenge leaderboard
     */
    public List<ChallengeParticipation> getLeaderboard(Long challengeId) {
        return participationRepository.findLeaderboardByChallengeId(challengeId);
    }

    /**
     * Get paginated leaderboard
     */
    public Page<ChallengeParticipation> getLeaderboard(Long challengeId, Pageable pageable) {
        return participationRepository.findLeaderboardByChallengeId(challengeId, pageable);
    }

    /**
     * Get team leaderboard
     */
    public List<Object[]> getTeamLeaderboard(Long challengeId) {
        return participationRepository.findTeamLeaderboardByChallengeId(challengeId);
    }

    /**
     * Get user's rank in challenge
     */
    public Integer getUserRank(Long challengeId, Long userId) {
        return participationRepository.findUserRankInChallenge(challengeId, userId);
    }

    /**
     * Update challenge status
     */
    public GroupChallenge updateStatus(Long challengeId, ChallengeStatus status) {
        Optional<GroupChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isEmpty()) {
            throw new RuntimeException("Challenge not found");
        }

        GroupChallenge challenge = challengeOpt.get();
        challenge.setStatus(status);
        return challengeRepository.save(challenge);
    }

    /**
     * Delete challenge
     */
    public void deleteChallenge(Long challengeId, Long userId) {
        Optional<GroupChallenge> challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isEmpty()) {
            throw new RuntimeException("Challenge not found");
        }

        GroupChallenge challenge = challengeOpt.get();

        // Only creator can delete
        if (!challenge.getCreator().getId().equals(userId)) {
            throw new RuntimeException("Only the creator can delete this challenge");
        }

        challengeRepository.delete(challenge);
    }

    /**
     * Update ranks for all participants in a challenge
     */
    private void updateRanks(Long challengeId) {
        List<ChallengeParticipation> leaderboard = participationRepository.findLeaderboardByChallengeId(challengeId);
        int rank = 1;
        for (ChallengeParticipation p : leaderboard) {
            p.setRank(rank++);
            participationRepository.save(p);
        }
    }

    /**
     * Start pending challenges that are ready
     */
    public void startPendingChallenges() {
        List<GroupChallenge> pendingChallenges = challengeRepository.findPendingChallengesReadyToStart(LocalDate.now());

        for (GroupChallenge challenge : pendingChallenges) {
            challenge.setStatus(ChallengeStatus.ACTIVE);
            challengeRepository.save(challenge);

            // Notify all participants
            for (ChallengeParticipation p : challenge.getParticipants()) {
                String title = "Challenge Started! 🚀";
                String message = String.format("'%s' has begun! Good luck!", challenge.getName());
                notificationService.createNotification(p.getUser(), title, message,
                        NotificationType.CHALLENGE_UPDATE);
            }
        }
    }

    /**
     * End active challenges that are complete
     */
    public void endActiveChallenges() {
        List<GroupChallenge> activeChallenges = challengeRepository.findActiveChallengesReadyToEnd(LocalDate.now());

        for (GroupChallenge challenge : activeChallenges) {
            challenge.setStatus(ChallengeStatus.COMPLETED);
            challengeRepository.save(challenge);

            // Determine winner
            List<ChallengeParticipation> leaderboard = getLeaderboard(challenge.getId());
            if (!leaderboard.isEmpty()) {
                ChallengeParticipation winner = leaderboard.get(0);

                // Notify all participants
                for (ChallengeParticipation p : challenge.getParticipants()) {
                    String title = "Challenge Complete! 🏆";
                    String message = String.format("'%s' has ended! Winner: %s with %d completions!",
                            challenge.getName(), winner.getUser().getUsername(), winner.getCompletionCount());
                    notificationService.createNotification(p.getUser(), title, message,
                            NotificationType.CHALLENGE_UPDATE);
                }
            }
        }
    }
}
