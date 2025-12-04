package com.habittracker.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Challenge Participation Entity
 * 
 * Tracks user participation in group challenges with completion counts
 */
@Entity
@Table(name = "challenge_participation", uniqueConstraints = @UniqueConstraint(columnNames = { "challenge_id",
        "user_id" }))
@EntityListeners(AuditingEntityListener.class)
public class ChallengeParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private GroupChallenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "completion_count")
    private Integer completionCount = 0;

    @Column(name = "rank")
    private Integer rank;

    @CreatedDate
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    // Constructors
    public ChallengeParticipation() {
    }

    public ChallengeParticipation(GroupChallenge challenge, User user) {
        this.challenge = challenge;
        this.user = user;
    }

    public ChallengeParticipation(GroupChallenge challenge, User user, String teamName) {
        this.challenge = challenge;
        this.user = user;
        this.teamName = teamName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GroupChallenge getChallenge() {
        return challenge;
    }

    public void setChallenge(GroupChallenge challenge) {
        this.challenge = challenge;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getCompletionCount() {
        return completionCount;
    }

    public void setCompletionCount(Integer completionCount) {
        this.completionCount = completionCount;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    // Helper method
    public void incrementCompletion() {
        this.completionCount++;
    }
}
