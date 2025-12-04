package com.habittracker.service;

import com.habittracker.model.ActivityFeed;
import com.habittracker.model.ActivityType;
import com.habittracker.model.User;
import com.habittracker.repository.ActivityFeedRepository;
import com.habittracker.repository.UserRepository;
import com.habittracker.websocket.NotificationWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Activity Feed Service
 * 
 * Manages the social activity feed for friend interactions
 */
@Service
@Transactional
public class ActivityFeedService {

    @Autowired
    private ActivityFeedRepository activityFeedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationWebSocketHandler webSocketHandler;

    @Autowired
    private NotificationService notificationService;

    /**
     * Create a new activity
     */
    public ActivityFeed createActivity(User user, ActivityType activityType, String description) {
        ActivityFeed activity = new ActivityFeed(user, activityType, description);
        ActivityFeed savedActivity = activityFeedRepository.save(activity);

        // Send real-time update to friends via WebSocket
        List<User> friends = userRepository.findFriendsByUserId(user.getId());
        for (User friend : friends) {
            webSocketHandler.sendFriendActivity(friend.getId(), savedActivity);
        }

        return savedActivity;
    }

    /**
     * Create activity for habit completion
     */
    public ActivityFeed createHabitCompletionActivity(User user, String habitName) {
        String description = String.format("%s completed '%s'", user.getUsername(), habitName);
        ActivityFeed activity = new ActivityFeed(user, ActivityType.HABIT_COMPLETED, habitName, description);
        ActivityFeed savedActivity = activityFeedRepository.save(activity);

        // Notify friends via WebSocket and push notifications
        List<User> friends = userRepository.findFriendsByUserId(user.getId());
        for (User friend : friends) {
            webSocketHandler.sendFriendActivity(friend.getId(), savedActivity);
            // Send social proof notification
            notificationService.createFriendCompletedHabitNotification(friend, user.getUsername(), habitName);
        }

        return savedActivity;
    }

    /**
     * Create activity for streak milestone
     */
    public ActivityFeed createStreakMilestoneActivity(User user, String habitName, Integer streakCount) {
        String description = String.format("%s reached a %d-day streak for '%s'! 🔥",
                user.getUsername(), streakCount, habitName);

        ActivityFeed activity = new ActivityFeed(user, ActivityType.STREAK_MILESTONE, habitName, description);
        activity.setStreakCount(streakCount);

        ActivityFeed savedActivity = activityFeedRepository.save(activity);

        // Notify friends via WebSocket and push notifications
        List<User> friends = userRepository.findFriendsByUserId(user.getId());
        for (User friend : friends) {
            webSocketHandler.sendFriendActivity(friend.getId(), savedActivity);
            // Send streak milestone notification
            notificationService.createFriendStreakMilestoneNotification(
                    friend, user.getUsername(), habitName, streakCount);
        }

        return savedActivity;
    }

    /**
     * Get friend activity feed (paginated)
     */
    public Page<ActivityFeed> getFriendActivityFeed(Long userId, Pageable pageable) {
        List<User> friends = userRepository.findFriendsByUserId(userId);
        List<Long> friendIds = friends.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        if (friendIds.isEmpty()) {
            return Page.empty(pageable);
        }

        return activityFeedRepository.findByFriendIdsOrderByCreatedAtDesc(friendIds, pageable);
    }

    /**
     * Get user's own activity feed
     */
    public Page<ActivityFeed> getUserActivityFeed(Long userId, Pageable pageable) {
        return activityFeedRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Get friend activity feed filtered by type
     */
    public Page<ActivityFeed> getFriendActivityFeedByType(Long userId, ActivityType activityType, Pageable pageable) {
        List<User> friends = userRepository.findFriendsByUserId(userId);
        List<Long> friendIds = friends.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        if (friendIds.isEmpty()) {
            return Page.empty(pageable);
        }

        return activityFeedRepository.findByFriendIdsAndActivityTypeOrderByCreatedAtDesc(
                friendIds, activityType, pageable);
    }

    /**
     * Get friend activity feed within date range
     */
    public Page<ActivityFeed> getFriendActivityFeedByDateRange(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        List<User> friends = userRepository.findFriendsByUserId(userId);
        List<Long> friendIds = friends.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        if (friendIds.isEmpty()) {
            return Page.empty(pageable);
        }

        return activityFeedRepository.findByFriendIdsAndDateRangeOrderByCreatedAtDesc(
                friendIds, startDate, endDate, pageable);
    }

    /**
     * Get count of friend activities today
     */
    public Long getFriendActivitiesCountToday(Long userId) {
        List<User> friends = userRepository.findFriendsByUserId(userId);
        List<Long> friendIds = friends.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        if (friendIds.isEmpty()) {
            return 0L;
        }

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return activityFeedRepository.countFriendActivitiesToday(friendIds, startOfDay);
    }

    /**
     * Get activity statistics for a user
     */
    public Long getUserActivityCount(Long userId) {
        return activityFeedRepository.countByUserId(userId);
    }
}
