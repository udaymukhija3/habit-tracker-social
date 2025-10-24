package com.habittracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "competition_participants")
public class CompetitionParticipant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private Integer score = 0;
    private Integer rank = 0;
    private LocalDateTime joinedAt;
    
    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }
    
    // Constructors
    public CompetitionParticipant() {}
    
    public CompetitionParticipant(Competition competition, User user) {
        this.competition = competition;
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
