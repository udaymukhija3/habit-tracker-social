package com.habittracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "streaks")
public class Streak {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull
    @Column(name = "current_streak")
    private Integer currentStreak = 0;
    
    @Column(name = "longest_streak")
    private Integer longestStreak = 0;
    
    @Column(name = "last_completion_date")
    private LocalDateTime lastCompletionDate;
    
    @Column(name = "streak_start_date")
    private LocalDateTime streakStartDate;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Streak() {}
    
    public Streak(Habit habit, User user) {
        this.habit = habit;
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Habit getHabit() { return habit; }
    public void setHabit(Habit habit) { this.habit = habit; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Integer getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(Integer currentStreak) { this.currentStreak = currentStreak; }
    
    public Integer getLongestStreak() { return longestStreak; }
    public void setLongestStreak(Integer longestStreak) { this.longestStreak = longestStreak; }
    
    public LocalDateTime getLastCompletionDate() { return lastCompletionDate; }
    public void setLastCompletionDate(LocalDateTime lastCompletionDate) { this.lastCompletionDate = lastCompletionDate; }
    
    public LocalDateTime getStreakStartDate() { return streakStartDate; }
    public void setStreakStartDate(LocalDateTime streakStartDate) { this.streakStartDate = streakStartDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
