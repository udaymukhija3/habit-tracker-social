package com.habittracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "habits")
public class Habit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private HabitType type;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private HabitFrequency frequency;
    
    @NotNull
    @Column(name = "target_value")
    private Integer targetValue;
    
    @NotNull
    @Column(name = "target_unit")
    private String targetUnit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<HabitCompletion> completions;
    
    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Streak> streaks;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive = true;
    
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
    public Habit() {}
    
    public Habit(String name, String description, HabitType type, HabitFrequency frequency, 
                 Integer targetValue, String targetUnit, User user) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.frequency = frequency;
        this.targetValue = targetValue;
        this.targetUnit = targetUnit;
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public HabitType getType() { return type; }
    public void setType(HabitType type) { this.type = type; }
    
    public HabitFrequency getFrequency() { return frequency; }
    public void setFrequency(HabitFrequency frequency) { this.frequency = frequency; }
    
    public Integer getTargetValue() { return targetValue; }
    public void setTargetValue(Integer targetValue) { this.targetValue = targetValue; }
    
    public String getTargetUnit() { return targetUnit; }
    public void setTargetUnit(String targetUnit) { this.targetUnit = targetUnit; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Set<HabitCompletion> getCompletions() { return completions; }
    public void setCompletions(Set<HabitCompletion> completions) { this.completions = completions; }
    
    public Set<Streak> getStreaks() { return streaks; }
    public void setStreaks(Set<Streak> streaks) { this.streaks = streaks; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
