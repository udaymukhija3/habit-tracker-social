package com.habittracker.service;

import com.habittracker.model.Friendship;
import com.habittracker.model.FriendshipStatus;
import com.habittracker.model.Role;
import com.habittracker.model.User;
import com.habittracker.repository.FriendshipRepository;
import com.habittracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FriendshipService friendshipService;

    private User user1;
    private User user2;
    private Friendship friendship;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setRole(Role.USER);

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setRole(Role.USER);

        friendship = new Friendship(user1, user2);
        friendship.setId(1L);
        friendship.setStatus(FriendshipStatus.PENDING);
    }

    @Test
    void testSendFriendRequest_Success() {
        // Given
        when(friendshipRepository.findByRequesterIdAndAddresseeId(1L, 2L)).thenReturn(Optional.empty());
        when(friendshipRepository.findByRequesterIdAndAddresseeId(2L, 1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);
        doNothing().when(notificationService).createFriendRequestNotification(any(), anyString());

        // When
        Friendship result = friendshipService.sendFriendRequest(1L, 2L);

        // Then
        assertNotNull(result);
        assertEquals(FriendshipStatus.PENDING, result.getStatus());
        verify(friendshipRepository).save(any(Friendship.class));
        verify(notificationService).createFriendRequestNotification(any(), anyString());
    }

    @Test
    void testSendFriendRequest_SelfRequest() {
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            friendshipService.sendFriendRequest(1L, 1L);
        });
    }

    @Test
    void testSendFriendRequest_AlreadyExists() {
        // Given
        when(friendshipRepository.findByRequesterIdAndAddresseeId(1L, 2L)).thenReturn(Optional.of(friendship));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            friendshipService.sendFriendRequest(1L, 2L);
        });
    }

    @Test
    void testAcceptFriendRequest_Success() {
        // Given
        friendship.setStatus(FriendshipStatus.PENDING);
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(friendshipRepository.save(friendship)).thenReturn(friendship);

        // When
        Friendship result = friendshipService.acceptFriendRequest(1L, 2L);

        // Then
        assertNotNull(result);
        assertEquals(FriendshipStatus.ACCEPTED, result.getStatus());
        verify(friendshipRepository).save(friendship);
    }

    @Test
    void testAcceptFriendRequest_NotFound() {
        // Given
        when(friendshipRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            friendshipService.acceptFriendRequest(1L, 2L);
        });
    }

    @Test
    void testDeclineFriendRequest_Success() {
        // Given
        friendship.setStatus(FriendshipStatus.PENDING);
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(friendshipRepository.save(friendship)).thenReturn(friendship);

        // When
        Friendship result = friendshipService.declineFriendRequest(1L, 2L);

        // Then
        assertNotNull(result);
        assertEquals(FriendshipStatus.DECLINED, result.getStatus());
        verify(friendshipRepository).save(friendship);
    }

    @Test
    void testRemoveFriend_Success() {
        // Given
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        doNothing().when(friendshipRepository).delete(friendship);

        // When
        friendshipService.removeFriend(1L, 1L);

        // Then
        verify(friendshipRepository).delete(friendship);
    }

    @Test
    void testGetPendingRequests() {
        // Given
        List<Friendship> requests = Arrays.asList(friendship);
        when(friendshipRepository.findPendingFriendRequestsByUserId(2L)).thenReturn(requests);

        // When
        List<Friendship> result = friendshipService.getPendingRequests(2L);

        // Then
        assertEquals(1, result.size());
        verify(friendshipRepository).findPendingFriendRequestsByUserId(2L);
    }

    @Test
    void testGetFriends() {
        // Given
        List<User> friends = Arrays.asList(user1);
        when(userRepository.findFriendsByUserId(2L)).thenReturn(friends);

        // When
        List<User> result = friendshipService.getFriends(2L);

        // Then
        assertEquals(1, result.size());
        verify(userRepository).findFriendsByUserId(2L);
    }

    @Test
    void testAreFriends_True() {
        // Given
        when(friendshipRepository.existsByRequesterIdAndAddresseeIdAndStatus(1L, 2L, FriendshipStatus.ACCEPTED))
                .thenReturn(true);

        // When
        boolean result = friendshipService.areFriends(1L, 2L);

        // Then
        assertTrue(result);
    }

    @Test
    void testAreFriends_False() {
        // Given
        when(friendshipRepository.existsByRequesterIdAndAddresseeIdAndStatus(1L, 2L, FriendshipStatus.ACCEPTED))
                .thenReturn(false);
        when(friendshipRepository.existsByRequesterIdAndAddresseeIdAndStatus(2L, 1L, FriendshipStatus.ACCEPTED))
                .thenReturn(false);

        // When
        boolean result = friendshipService.areFriends(1L, 2L);

        // Then
        assertFalse(result);
    }
}

