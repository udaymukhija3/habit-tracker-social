package com.habittracker.controller;

import com.habittracker.model.*;
import com.habittracker.service.GroupChallengeService;
import com.habittracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Group Challenge Controller
 * 
 * Handles challenge-related endpoints for team competitions
 */
@RestController
@RequestMapping("/api/v1/challenges")
public class GroupChallengeController {

    @Autowired
    private GroupChallengeService challengeService;

    @Autowired
    private UserService userService;

    /**
     * Create a new challenge
     */
    @PostMapping
    public ResponseEntity<GroupChallenge> createChallenge(
            @RequestBody CreateChallengeRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        GroupChallenge challenge = challengeService.createChallenge(
                user,
                request.getName(),
                request.getDescription(),
                request.getChallengeType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getTargetCompletions());

        return ResponseEntity.ok(challenge);
    }

    /**
     * Get all active challenges
     */
    @GetMapping
    public ResponseEntity<Page<GroupChallenge>> getActiveChallenges(Pageable pageable) {
        Page<GroupChallenge> challenges = challengeService.getActiveChallenges(pageable);
        return ResponseEntity.ok(challenges);
    }

    /**
     * Get challenge by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<GroupChallenge> getChallenge(@PathVariable Long id) {
        Optional<GroupChallenge> challenge = challengeService.findById(id);
        return challenge.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get challenges user is participating in
     */
    @GetMapping("/my")
    public ResponseEntity<Page<GroupChallenge>> getMyChallenges(
            Authentication authentication,
            Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        Page<GroupChallenge> challenges = challengeService.getUserChallenges(user.getId(), pageable);
        return ResponseEntity.ok(challenges);
    }

    /**
     * Get challenges created by user
     */
    @GetMapping("/created")
    public ResponseEntity<Page<GroupChallenge>> getCreatedChallenges(
            Authentication authentication,
            Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        Page<GroupChallenge> challenges = challengeService.getChallengesCreatedByUser(user.getId(), pageable);
        return ResponseEntity.ok(challenges);
    }

    /**
     * Join a challenge
     */
    @PostMapping("/{id}/join")
    public ResponseEntity<ChallengeParticipation> joinChallenge(
            @PathVariable Long id,
            @RequestBody(required = false) JoinChallengeRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String teamName = request != null ? request.getTeamName() : null;

        ChallengeParticipation participation = challengeService.joinChallenge(id, user, teamName);
        return ResponseEntity.ok(participation);
    }

    /**
     * Invite a friend to challenge
     */
    @PostMapping("/{id}/invite")
    public ResponseEntity<Void> inviteFriend(
            @PathVariable Long id,
            @RequestBody InviteFriendRequest request,
            Authentication authentication) {
        User inviter = (User) authentication.getPrincipal();
        Optional<User> friendOpt = userService.findById(request.getFriendId());

        if (friendOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        challengeService.inviteFriend(id, inviter, friendOpt.get());
        return ResponseEntity.ok().build();
    }

    /**
     * Get challenge leaderboard
     */
    @GetMapping("/{id}/leaderboard")
    public ResponseEntity<List<ChallengeParticipation>> getLeaderboard(@PathVariable Long id) {
        List<ChallengeParticipation> leaderboard = challengeService.getLeaderboard(id);
        return ResponseEntity.ok(leaderboard);
    }

    /**
     * Get team leaderboard
     */
    @GetMapping("/{id}/leaderboard/teams")
    public ResponseEntity<List<Object[]>> getTeamLeaderboard(@PathVariable Long id) {
        List<Object[]> teamLeaderboard = challengeService.getTeamLeaderboard(id);
        return ResponseEntity.ok(teamLeaderboard);
    }

    /**
     * Get user's rank in challenge
     */
    @GetMapping("/{id}/rank")
    public ResponseEntity<Integer> getUserRank(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Integer rank = challengeService.getUserRank(id, user.getId());
        return ResponseEntity.ok(rank);
    }

    /**
     * Update challenge status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<GroupChallenge> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        // Verify user is creator (handled in service)
        Optional<GroupChallenge> challengeOpt = challengeService.findById(id);
        if (challengeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!challengeOpt.get().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        GroupChallenge updated = challengeService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete challenge
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        challengeService.deleteChallenge(id, user.getId());
        return ResponseEntity.ok().build();
    }

    // DTO Classes
    public static class CreateChallengeRequest {
        private String name;
        private String description;
        private ChallengeType challengeType;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer targetCompletions;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public ChallengeType getChallengeType() {
            return challengeType;
        }

        public void setChallengeType(ChallengeType challengeType) {
            this.challengeType = challengeType;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public Integer getTargetCompletions() {
            return targetCompletions;
        }

        public void setTargetCompletions(Integer targetCompletions) {
            this.targetCompletions = targetCompletions;
        }
    }

    public static class JoinChallengeRequest {
        private String teamName;

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }
    }

    public static class InviteFriendRequest {
        private Long friendId;

        public Long getFriendId() {
            return friendId;
        }

        public void setFriendId(Long friendId) {
            this.friendId = friendId;
        }
    }

    public static class UpdateStatusRequest {
        private ChallengeStatus status;

        public ChallengeStatus getStatus() {
            return status;
        }

        public void setStatus(ChallengeStatus status) {
            this.status = status;
        }
    }
}
