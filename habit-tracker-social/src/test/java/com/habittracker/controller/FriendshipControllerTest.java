package com.habittracker.controller;

import com.habittracker.model.Friendship;
import com.habittracker.model.FriendshipStatus;
import com.habittracker.model.Role;
import com.habittracker.model.User;
import com.habittracker.service.FriendshipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FriendshipController.class)
public class FriendshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FriendshipService friendshipService;

    private User createTestUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setRole(Role.USER);
        return user;
    }

    private Friendship createTestFriendship(Long id, User requester, User addressee, FriendshipStatus status) {
        Friendship friendship = new Friendship(requester, addressee);
        friendship.setId(id);
        friendship.setStatus(status);
        return friendship;
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testSendFriendRequest() throws Exception {
        // Given
        User requester = createTestUser(1L, "testuser");
        User addressee = createTestUser(2L, "frienduser");
        Friendship friendship = createTestFriendship(1L, requester, addressee, FriendshipStatus.PENDING);

        when(friendshipService.sendFriendRequest(1L, 2L)).thenReturn(friendship);

        // When & Then
        mockMvc.perform(post("/friends/request/2")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testAcceptFriendRequest() throws Exception {
        // Given
        User requester = createTestUser(2L, "requester");
        User addressee = createTestUser(1L, "testuser");
        Friendship friendship = createTestFriendship(1L, requester, addressee, FriendshipStatus.ACCEPTED);

        when(friendshipService.acceptFriendRequest(1L, 1L)).thenReturn(friendship);

        // When & Then
        mockMvc.perform(post("/friends/accept/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testDeclineFriendRequest() throws Exception {
        // Given
        User requester = createTestUser(2L, "requester");
        User addressee = createTestUser(1L, "testuser");
        Friendship friendship = createTestFriendship(1L, requester, addressee, FriendshipStatus.DECLINED);

        when(friendshipService.declineFriendRequest(1L, 1L)).thenReturn(friendship);

        // When & Then
        mockMvc.perform(post("/friends/decline/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DECLINED"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testRemoveFriend() throws Exception {
        // Given
        doNothing().when(friendshipService).removeFriend(1L, 1L);

        // When & Then
        mockMvc.perform(delete("/friends/1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(friendshipService).removeFriend(1L, 1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetPendingRequests() throws Exception {
        // Given
        User requester = createTestUser(2L, "requester");
        User addressee = createTestUser(1L, "testuser");
        Friendship friendship = createTestFriendship(1L, requester, addressee, FriendshipStatus.PENDING);
        List<Friendship> requests = Arrays.asList(friendship);

        when(friendshipService.getPendingRequests(1L)).thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/friends/requests/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetSentRequests() throws Exception {
        // Given
        User requester = createTestUser(1L, "testuser");
        User addressee = createTestUser(2L, "frienduser");
        Friendship friendship = createTestFriendship(1L, requester, addressee, FriendshipStatus.PENDING);
        List<Friendship> requests = Arrays.asList(friendship);

        when(friendshipService.getSentRequests(1L)).thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/friends/requests/sent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetFriends() throws Exception {
        // Given
        User friend1 = createTestUser(2L, "friend1");
        User friend2 = createTestUser(3L, "friend2");
        List<User> friends = Arrays.asList(friend1, friend2);

        when(friendshipService.getFriends(1L)).thenReturn(friends);

        // When & Then
        mockMvc.perform(get("/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("friend1"))
                .andExpect(jsonPath("$[1].username").value("friend2"));
    }
}

