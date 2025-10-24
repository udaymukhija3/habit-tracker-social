package com.habittracker.dto;

import com.habittracker.model.HabitFrequency;
import com.habittracker.model.HabitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class HabitDTO {
    
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    @NotNull
    private HabitType type;
    
    @NotNull
    private HabitFrequency frequency;
    
    @NotNull
    private Integer targetValue;
    
    @NotNull
    private String targetUnit;
    
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;
    
    // Constructors
    public HabitDTO() {}
    
    public HabitDTO(Long id, String name, String description, HabitType type, HabitFrequency frequency,
                   Integer targetValue, String targetUnit, Long userId, LocalDateTime createdAt,
                   LocalDateTime updatedAt, boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.frequency = frequency;
        this.targetValue = targetValue;
        this.targetUnit = targetUnit;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
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
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
