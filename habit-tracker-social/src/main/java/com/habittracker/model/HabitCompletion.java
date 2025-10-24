package com.habittracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "habit_completions")
public class HabitCompletion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;
    
    @NotNull
    @Column(name = "completion_date")
    private LocalDateTime completionDate;
    
    @Column(name = "completion_value")
    private Integer completionValue;
    
    private String notes;
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public HabitCompletion() {}
    
    public HabitCompletion(Habit habit, LocalDateTime completionDate, Integer completionValue) {
        this.habit = habit;
        this.completionDate = completionDate;
        this.completionValue = completionValue;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Habit getHabit() { return habit; }
    public void setHabit(Habit habit) { this.habit = habit; }
    
    public LocalDateTime getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDateTime completionDate) { this.completionDate = completionDate; }
    
    public Integer getCompletionValue() { return completionValue; }
    public void setCompletionValue(Integer completionValue) { this.completionValue = completionValue; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
