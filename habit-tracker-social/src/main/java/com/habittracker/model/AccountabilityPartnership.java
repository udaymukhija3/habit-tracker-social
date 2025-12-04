package com.habittracker.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Accountability Partnership Entity
 * 
 * Represents 1-on-1 accountability partnerships between users for mutual habit
 * support
 */
@Entity
@Table(name = "accountability_partnership", uniqueConstraints = @UniqueConstraint(columnNames = { "user1_id",
        "user2_id" }))
@EntityListeners(AuditingEntityListener.class)
public class AccountabilityPartnership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1; // The initiator

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2; // The partner

    @Column(name = "shared_goal", columnDefinition = "TEXT")
    private String sharedGoal;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnershipStatus status = PartnershipStatus.PENDING;

    @Column(name = "user1_completions")
    private Integer user1Completions = 0;

    @Column(name = "user2_completions")
    private Integer user2Completions = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public AccountabilityPartnership() {
    }

    public AccountabilityPartnership(User user1, User user2, String sharedGoal, LocalDate startDate) {
        this.user1 = user1;
        this.user2 = user2;
        this.sharedGoal = sharedGoal;
        this.startDate = startDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public String getSharedGoal() {
        return sharedGoal;
    }

    public void setSharedGoal(String sharedGoal) {
        this.sharedGoal = sharedGoal;
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

    public PartnershipStatus getStatus() {
        return status;
    }

    public void setStatus(PartnershipStatus status) {
        this.status = status;
    }

    public Integer getUser1Completions() {
        return user1Completions;
    }

    public void setUser1Completions(Integer user1Completions) {
        this.user1Completions = user1Completions;
    }

    public Integer getUser2Completions() {
        return user2Completions;
    }

    public void setUser2Completions(Integer user2Completions) {
        this.user2Completions = user2Completions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public boolean isActive() {
        return status == PartnershipStatus.ACTIVE;
    }

    public User getPartner(User user) {
        if (user.getId().equals(user1.getId())) {
            return user2;
        } else if (user.getId().equals(user2.getId())) {
            return user1;
        }
        return null;
    }

    public void incrementCompletions(Long userId) {
        if (userId.equals(user1.getId())) {
            user1Completions++;
        } else if (userId.equals(user2.getId())) {
            user2Completions++;
        }
    }

    public Integer getCompletions(Long userId) {
        if (userId.equals(user1.getId())) {
            return user1Completions;
        } else if (userId.equals(user2.getId())) {
            return user2Completions;
        }
        return 0;
    }
}
