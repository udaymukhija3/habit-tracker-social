package com.habittracker.controller;

import com.habittracker.model.ActivityFeed;
import com.habittracker.model.ActivityType;
import com.habittracker.model.User;
import com.habittracker.service.ActivityFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Activity Feed Controller
 * 
 * Handles friend activity feed endpoints
 */
@RestController
@RequestMapping("/api/v1/activity")
public class ActivityFeedController {

    @Autowired
    private ActivityFeedService activityFeedService;

    /**
     * Get friend activity feed (paginated)
     */
    @GetMapping("/feed")
    public ResponseEntity<Page<ActivityFeed>> getFriendActivityFeed(
            Authentication authentication,
            Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        Page<ActivityFeed> feed = activityFeedService.getFriendActivityFeed(user.getId(), pageable);
        return ResponseEntity.ok(feed);
    }

    /**
     * Get user's own activity feed
     */
    @GetMapping("/feed/{userId}")
    public ResponseEntity<Page<ActivityFeed>> getUserActivityFeed(
            @PathVariable Long userId,
            Authentication authentication,
            Pageable pageable) {
        // TODO: Add authorization check - only friends can see each other's feeds
        Page<ActivityFeed> feed = activityFeedService.getUserActivityFeed(userId, pageable);
        return ResponseEntity.ok(feed);
    }

    /**
     * Get friend activity feed filtered by type
     */
    @GetMapping("/feed/type/{activityType}")
    public ResponseEntity<Page<ActivityFeed>> getFriendActivityFeedByType(
            @PathVariable ActivityType activityType,
            Authentication authentication,
            Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        Page<ActivityFeed> feed = activityFeedService.getFriendActivityFeedByType(
                user.getId(), activityType, pageable);
        return ResponseEntity.ok(feed);
    }

    /**
     * Get friend activity feed within date range
     */
    @GetMapping("/feed/range")
    public ResponseEntity<Page<ActivityFeed>> getFriendActivityFeedByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication,
            Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        Page<ActivityFeed> feed = activityFeedService.getFriendActivityFeedByDateRange(
                user.getId(), startDate, endDate, pageable);
        return ResponseEntity.ok(feed);
    }

    /**
     * Get activity statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ActivityStats> getActivityStats(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Long userActivityCount = activityFeedService.getUserActivityCount(user.getId());
        Long friendActivitiesToday = activityFeedService.getFriendActivitiesCountToday(user.getId());

        ActivityStats stats = new ActivityStats(userActivityCount, friendActivitiesToday);
        return ResponseEntity.ok(stats);
    }

    /**
     * Activity Statistics DTO
     */
    public static class ActivityStats {
        private Long userActivityCount;
        private Long friendActivitiesToday;

        public ActivityStats(Long userActivityCount, Long friendActivitiesToday) {
            this.userActivityCount = userActivityCount;
            this.friendActivitiesToday = friendActivitiesToday;
        }

        public Long getUserActivityCount() {
            return userActivityCount;
        }

        public void setUserActivityCount(Long userActivityCount) {
            this.userActivityCount = userActivityCount;
        }

        public Long getFriendActivitiesToday() {
            return friendActivitiesToday;
        }

        public void setFriendActivitiesToday(Long friendActivitiesToday) {
            this.friendActivitiesToday = friendActivitiesToday;
        }
    }
}
