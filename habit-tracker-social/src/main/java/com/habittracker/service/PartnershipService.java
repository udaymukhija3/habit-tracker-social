package com.habittracker.service;

import com.habittracker.model.*;
import com.habittracker.repository.AccountabilityPartnershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Partnership Service
 * 
 * Manages 1-on-1 accountability partnerships for mutual habit support
 */
@Service
@Transactional
public class PartnershipService {

    @Autowired
    private AccountabilityPartnershipRepository partnershipRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ActivityFeedService activityFeedService;

    /**
     * Create a partnership request
     */
    public AccountabilityPartnership createPartnershipRequest(User initiator, User partner,
            String sharedGoal, LocalDate startDate) {
        // Check if partnership already exists
        if (partnershipRepository.existsByUserPair(initiator.getId(), partner.getId())) {
            throw new RuntimeException("Partnership already exists between these users");
        }

        AccountabilityPartnership partnership = new AccountabilityPartnership(
                initiator, partner, sharedGoal, startDate);

        AccountabilityPartnership saved = partnershipRepository.save(partnership);

        // Notify the partner
        String title = "Partnership Request! 🤝";
        String message = String.format("%s wants to be your accountability partner for: %s",
                initiator.getUsername(), sharedGoal);
        notificationService.createNotification(partner, title, message, NotificationType.FRIEND_REQUEST);

        return saved;
    }

    /**
     * Accept a partnership request
     */
    public AccountabilityPartnership acceptPartnership(Long partnershipId, Long userId) {
        Optional<AccountabilityPartnership> partnershipOpt = partnershipRepository.findById(partnershipId);
        if (partnershipOpt.isEmpty()) {
            throw new RuntimeException("Partnership not found");
        }

        AccountabilityPartnership partnership = partnershipOpt.get();

        // Verify the user is the recipient (user2)
        if (!partnership.getUser2().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to accept this partnership");
        }

        if (partnership.getStatus() != PartnershipStatus.PENDING) {
            throw new RuntimeException("Partnership is not pending");
        }

        partnership.setStatus(PartnershipStatus.ACTIVE);
        AccountabilityPartnership saved = partnershipRepository.save(partnership);

        // Notify the initiator
        String title = "Partnership Accepted! 🎉";
        String message = String.format("%s accepted your partnership request!",
                partnership.getUser2().getUsername());
        notificationService.createNotification(partnership.getUser1(), title, message,
                NotificationType.FRIEND_ACHIEVEMENT);

        // Create activity feed entry
        activityFeedService.createActivity(partnership.getUser2(), ActivityType.PARTNERSHIP_STARTED,
                partnership.getUser2().getUsername() + " partnered with " + partnership.getUser1().getUsername());

        return saved;
    }

    /**
     * Decline a partnership request
     */
    public void declinePartnership(Long partnershipId, Long userId) {
        Optional<AccountabilityPartnership> partnershipOpt = partnershipRepository.findById(partnershipId);
        if (partnershipOpt.isEmpty()) {
            throw new RuntimeException("Partnership not found");
        }

        AccountabilityPartnership partnership = partnershipOpt.get();

        // Verify the user is the recipient (user2)
        if (!partnership.getUser2().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to decline this partnership");
        }

        partnership.setStatus(PartnershipStatus.CANCELLED);
        partnershipRepository.save(partnership);
    }

    /**
     * Get partnership by ID
     */
    public Optional<AccountabilityPartnership> findById(Long id) {
        return partnershipRepository.findById(id);
    }

    /**
     * Get user's partnerships (paginated)
     */
    public Page<AccountabilityPartnership> getUserPartnerships(Long userId, Pageable pageable) {
        return partnershipRepository.findByUserId(userId, pageable);
    }

    /**
     * Get user's active partnerships
     */
    public List<AccountabilityPartnership> getActivePartnerships(Long userId) {
        return partnershipRepository.findActiveByUserId(userId);
    }

    /**
     * Get pending partnership requests for user
     */
    public List<AccountabilityPartnership> getPendingRequests(Long userId) {
        return partnershipRepository.findPendingRequestsForUser(userId);
    }

    /**
     * Record habit completion for partnership
     */
    public void recordCompletion(Long userId) {
        List<AccountabilityPartnership> activePartnerships = partnershipRepository.findActiveByUserId(userId);

        for (AccountabilityPartnership partnership : activePartnerships) {
            partnership.incrementCompletions(userId);
            partnershipRepository.save(partnership);

            // Notify partner
            User partner = partnership.getPartner(partnershipRepository.findById(partnership.getId())
                    .map(p -> p.getUser1().getId().equals(userId) ? p.getUser1() : p.getUser2())
                    .orElse(null));

            if (partner != null) {
                User actualPartner = partnership.getPartner(
                        partnership.getUser1().getId().equals(userId) ? partnership.getUser1()
                                : partnership.getUser2());
                // Partner notification handled by activity feed
            }
        }
    }

    /**
     * Get partnership statistics
     */
    public PartnershipStats getPartnershipStats(Long partnershipId) {
        Optional<AccountabilityPartnership> partnershipOpt = partnershipRepository.findById(partnershipId);
        if (partnershipOpt.isEmpty()) {
            return null;
        }

        AccountabilityPartnership partnership = partnershipOpt.get();
        return new PartnershipStats(
                partnership.getUser1Completions(),
                partnership.getUser2Completions(),
                partnership.getUser1().getUsername(),
                partnership.getUser2().getUsername());
    }

    /**
     * End a partnership
     */
    public void endPartnership(Long partnershipId, Long userId) {
        Optional<AccountabilityPartnership> partnershipOpt = partnershipRepository.findById(partnershipId);
        if (partnershipOpt.isEmpty()) {
            throw new RuntimeException("Partnership not found");
        }

        AccountabilityPartnership partnership = partnershipOpt.get();

        // Verify the user is part of the partnership
        if (!partnership.getUser1().getId().equals(userId) &&
                !partnership.getUser2().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to end this partnership");
        }

        partnership.setStatus(PartnershipStatus.COMPLETED);
        partnership.setEndDate(LocalDate.now());
        partnershipRepository.save(partnership);
    }

    /**
     * Check if partner needs encouragement (no completions today)
     */
    public void checkPartnerNeedsEncouragement(Long userId, LocalDate date) {
        List<AccountabilityPartnership> activePartnerships = partnershipRepository.findActiveByUserId(userId);

        for (AccountabilityPartnership partnership : activePartnerships) {
            User partner = partnership.getPartner(
                    partnership.getUser1().getId().equals(userId) ? partnership.getUser1() : partnership.getUser2());

            if (partner != null) {
                // This would check if partner completed habits today
                // For now, just send encouragement notification
                notificationService.createPartnerNeedsEncouragementNotification(
                        partnership.getUser1().getId().equals(userId) ? partnership.getUser1() : partnership.getUser2(),
                        partner.getUsername());
            }
        }
    }

    /**
     * Partnership Statistics DTO
     */
    public static class PartnershipStats {
        private Integer user1Completions;
        private Integer user2Completions;
        private String user1Name;
        private String user2Name;

        public PartnershipStats(Integer user1Completions, Integer user2Completions,
                String user1Name, String user2Name) {
            this.user1Completions = user1Completions;
            this.user2Completions = user2Completions;
            this.user1Name = user1Name;
            this.user2Name = user2Name;
        }

        // Getters
        public Integer getUser1Completions() {
            return user1Completions;
        }

        public Integer getUser2Completions() {
            return user2Completions;
        }

        public String getUser1Name() {
            return user1Name;
        }

        public String getUser2Name() {
            return user2Name;
        }
    }
}
