package com.habittracker.service;

import com.habittracker.model.Friendship;
import com.habittracker.model.FriendshipStatus;
import com.habittracker.model.User;
import com.habittracker.repository.FriendshipRepository;
import com.habittracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FriendshipService {
    
    @Autowired
    private FriendshipRepository friendshipRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public Friendship sendFriendRequest(Long requesterId, Long addresseeId) {
        if (requesterId.equals(addresseeId)) {
            throw new RuntimeException("Cannot send friend request to yourself");
        }
        
        // Check if friendship already exists
        Optional<Friendship> existingFriendship = friendshipRepository.findByRequesterIdAndAddresseeId(requesterId, addresseeId);
        if (existingFriendship.isPresent()) {
            throw new RuntimeException("Friend request already exists");
        }
        
        // Check reverse friendship
        Optional<Friendship> reverseFriendship = friendshipRepository.findByRequesterIdAndAddresseeId(addresseeId, requesterId);
        if (reverseFriendship.isPresent()) {
            throw new RuntimeException("Friend request already exists");
        }
        
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        User addressee = userRepository.findById(addresseeId)
                .orElseThrow(() -> new RuntimeException("Addressee not found"));
        
        Friendship friendship = new Friendship(requester, addressee);
        Friendship savedFriendship = friendshipRepository.save(friendship);
        
        // Send notification
        notificationService.createFriendRequestNotification(addressee, requester.getUsername());
        
        return savedFriendship;
    }
    
    public Friendship acceptFriendRequest(Long friendshipId, Long userId) {
        Optional<Friendship> friendshipOpt = friendshipRepository.findById(friendshipId);
        if (friendshipOpt.isEmpty()) {
            throw new RuntimeException("Friendship not found");
        }
        
        Friendship friendship = friendshipOpt.get();
        if (!friendship.getAddressee().getId().equals(userId)) {
            throw new RuntimeException("You can only accept your own friend requests");
        }
        
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new RuntimeException("Friend request is not pending");
        }
        
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipRepository.save(friendship);
    }
    
    public Friendship declineFriendRequest(Long friendshipId, Long userId) {
        Optional<Friendship> friendshipOpt = friendshipRepository.findById(friendshipId);
        if (friendshipOpt.isEmpty()) {
            throw new RuntimeException("Friendship not found");
        }
        
        Friendship friendship = friendshipOpt.get();
        if (!friendship.getAddressee().getId().equals(userId)) {
            throw new RuntimeException("You can only decline your own friend requests");
        }
        
        friendship.setStatus(FriendshipStatus.DECLINED);
        return friendshipRepository.save(friendship);
    }
    
    public void removeFriend(Long friendshipId, Long userId) {
        Optional<Friendship> friendshipOpt = friendshipRepository.findById(friendshipId);
        if (friendshipOpt.isEmpty()) {
            throw new RuntimeException("Friendship not found");
        }
        
        Friendship friendship = friendshipOpt.get();
        if (!friendship.getRequester().getId().equals(userId) && !friendship.getAddressee().getId().equals(userId)) {
            throw new RuntimeException("You can only remove your own friendships");
        }
        
        friendshipRepository.delete(friendship);
    }
    
    public List<Friendship> getPendingRequests(Long userId) {
        return friendshipRepository.findPendingFriendRequestsByUserId(userId);
    }
    
    public List<Friendship> getSentRequests(Long userId) {
        return friendshipRepository.findSentFriendRequestsByUserId(userId);
    }
    
    public List<Friendship> getAcceptedFriendships(Long userId) {
        return friendshipRepository.findAcceptedFriendshipsByUserId(userId);
    }
    
    public List<User> getFriends(Long userId) {
        return userRepository.findFriendsByUserId(userId);
    }
    
    public boolean areFriends(Long userId1, Long userId2) {
        return friendshipRepository.existsByRequesterIdAndAddresseeIdAndStatus(userId1, userId2, FriendshipStatus.ACCEPTED) ||
               friendshipRepository.existsByRequesterIdAndAddresseeIdAndStatus(userId2, userId1, FriendshipStatus.ACCEPTED);
    }
}
