package com.habittracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendships")
public class Friendship {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addressee_id", nullable = false)
    private User addressee;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status = FriendshipStatus.PENDING;
    
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
    public Friendship() {}
    
    public Friendship(User requester, User addressee) {
        this.requester = requester;
        this.addressee = addressee;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getRequester() { return requester; }
    public void setRequester(User requester) { this.requester = requester; }
    
    public User getAddressee() { return addressee; }
    public void setAddressee(User addressee) { this.addressee = addressee; }
    
    public FriendshipStatus getStatus() { return status; }
    public void setStatus(FriendshipStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
