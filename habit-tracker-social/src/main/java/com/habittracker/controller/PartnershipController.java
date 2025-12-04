package com.habittracker.controller;

import com.habittracker.model.AccountabilityPartnership;
import com.habittracker.model.User;
import com.habittracker.service.PartnershipService;
import com.habittracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Partnership Controller
 * 
 * Handles accountability partnership endpoints for 1-on-1 support
 */
@RestController
@RequestMapping("/api/v1/partnerships")
public class PartnershipController {

    @Autowired
    private PartnershipService partnershipService;

    @Autowired
    private UserService userService;

    /**
     * Create a partnership request
     */
    @PostMapping
    public ResponseEntity<AccountabilityPartnership> createPartnershipRequest(
            @RequestBody CreatePartnershipRequest request,
            Authentication authentication) {
        User initiator = (User) authentication.getPrincipal();
        Optional<User> partnerOpt = userService.findById(request.getPartnerId());

        if (partnerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AccountabilityPartnership partnership = partnershipService.createPartnershipRequest(
                initiator,
                partnerOpt.get(),
                request.getSharedGoal(),
                request.getStartDate() != null ? request.getStartDate() : LocalDate.now());

        return ResponseEntity.ok(partnership);
    }

    /**
     * Get user's partnerships (paginated)
     */
    @GetMapping
    public ResponseEntity<Page<AccountabilityPartnership>> getMyPartnerships(
            Authentication authentication,
            Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        Page<AccountabilityPartnership> partnerships = partnershipService.getUserPartnerships(user.getId(), pageable);
        return ResponseEntity.ok(partnerships);
    }

    /**
     * Get partnership by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountabilityPartnership> getPartnership(@PathVariable Long id) {
        Optional<AccountabilityPartnership> partnership = partnershipService.findById(id);
        return partnership.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get active partnerships
     */
    @GetMapping("/active")
    public ResponseEntity<List<AccountabilityPartnership>> getActivePartnerships(
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<AccountabilityPartnership> partnerships = partnershipService.getActivePartnerships(user.getId());
        return ResponseEntity.ok(partnerships);
    }

    /**
     * Get pending partnership requests
     */
    @GetMapping("/pending")
    public ResponseEntity<List<AccountabilityPartnership>> getPendingRequests(
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<AccountabilityPartnership> requests = partnershipService.getPendingRequests(user.getId());
        return ResponseEntity.ok(requests);
    }

    /**
     * Accept a partnership request
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<AccountabilityPartnership> acceptPartnership(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        AccountabilityPartnership partnership = partnershipService.acceptPartnership(id, user.getId());
        return ResponseEntity.ok(partnership);
    }

    /**
     * Decline a partnership request
     */
    @PostMapping("/{id}/decline")
    public ResponseEntity<Void> declinePartnership(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        partnershipService.declinePartnership(id, user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Get partnership statistics
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<PartnershipService.PartnershipStats> getPartnershipStats(
            @PathVariable Long id) {
        PartnershipService.PartnershipStats stats = partnershipService.getPartnershipStats(id);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    /**
     * End a partnership
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> endPartnership(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        partnershipService.endPartnership(id, user.getId());
        return ResponseEntity.ok().build();
    }

    // DTO Classes
    public static class CreatePartnershipRequest {
        private Long partnerId;
        private String sharedGoal;
        private LocalDate startDate;

        // Getters and Setters
        public Long getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(Long partnerId) {
            this.partnerId = partnerId;
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
    }
}
