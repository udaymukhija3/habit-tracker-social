package com.habittracker.controller;

import com.habittracker.model.Friendship;
import com.habittracker.model.User;
import com.habittracker.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    @PostMapping("/request/{userId}")
    public ResponseEntity<Friendship> sendFriendRequest(@PathVariable Long userId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Friendship friendship = friendshipService.sendFriendRequest(currentUser.getId(), userId);
        return ResponseEntity.ok(friendship);
    }

    @PostMapping("/accept/{friendshipId}")
    public ResponseEntity<Friendship> acceptFriendRequest(@PathVariable Long friendshipId,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Friendship friendship = friendshipService.acceptFriendRequest(friendshipId, currentUser.getId());
        return ResponseEntity.ok(friendship);
    }

    @PostMapping("/decline/{friendshipId}")
    public ResponseEntity<Friendship> declineFriendRequest(@PathVariable Long friendshipId,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Friendship friendship = friendshipService.declineFriendRequest(friendshipId, currentUser.getId());
        return ResponseEntity.ok(friendship);
    }

    @DeleteMapping("/{friendshipId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long friendshipId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        friendshipService.removeFriend(friendshipId, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<List<Friendship>> getPendingRequests(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<Friendship> requests = friendshipService.getPendingRequests(currentUser.getId());
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/requests/sent")
    public ResponseEntity<List<Friendship>> getSentRequests(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<Friendship> requests = friendshipService.getSentRequests(currentUser.getId());
        return ResponseEntity.ok(requests);
    }

    @GetMapping
    public ResponseEntity<List<User>> getFriends(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<User> friends = friendshipService.getFriends(currentUser.getId());
        return ResponseEntity.ok(friends);
    }
}
