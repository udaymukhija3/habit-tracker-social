package com.habittracker.controller;

import com.habittracker.model.Habit;
import com.habittracker.model.HabitTemplate;
import com.habittracker.model.User;
import com.habittracker.service.HabitTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Habit Template Controller
 * 
 * Handles habit template endpoints for sharing and cloning
 */
@RestController
@RequestMapping("/api/v1/habits/templates")
public class HabitTemplateController {

    @Autowired
    private HabitTemplateService templateService;

    /**
     * Get popular templates
     */
    @GetMapping
    public ResponseEntity<Page<HabitTemplate>> getPopularTemplates(Pageable pageable) {
        Page<HabitTemplate> templates = templateService.getPopularTemplates(pageable);
        return ResponseEntity.ok(templates);
    }

    /**
     * Get templates from friends
     */
    @GetMapping("/friends")
    public ResponseEntity<Page<HabitTemplate>> getTemplatesFromFriends(
            Authentication authentication,
            Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        Page<HabitTemplate> templates = templateService.getTemplatesFromFriends(user.getId(), pageable);
        return ResponseEntity.ok(templates);
    }

    /**
     * Get user's own templates
     */
    @GetMapping("/my")
    public ResponseEntity<Page<HabitTemplate>> getMyTemplates(
            Authentication authentication,
            Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        Page<HabitTemplate> templates = templateService.getUserTemplates(user.getId(), pageable);
        return ResponseEntity.ok(templates);
    }

    /**
     * Search templates
     */
    @GetMapping("/search")
    public ResponseEntity<Page<HabitTemplate>> searchTemplates(
            @RequestParam String query,
            Pageable pageable) {
        Page<HabitTemplate> templates = templateService.searchTemplates(query, pageable);
        return ResponseEntity.ok(templates);
    }

    /**
     * Get template by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<HabitTemplate> getTemplate(@PathVariable Long id) {
        Optional<HabitTemplate> template = templateService.findById(id);
        return template.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Share a habit as template
     */
    @PostMapping("/share/{habitId}")
    public ResponseEntity<HabitTemplate> shareHabit(
            @PathVariable Long habitId,
            @RequestBody(required = false) ShareTemplateRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean isPublic = request == null || request.getIsPublic() == null || request.getIsPublic();

        HabitTemplate template = templateService.shareHabitAsTemplate(habitId, user, isPublic);
        return ResponseEntity.ok(template);
    }

    /**
     * Clone a template to create new habit
     */
    @PostMapping("/{id}/clone")
    public ResponseEntity<Habit> cloneTemplate(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Habit habit = templateService.cloneTemplate(id, user);
        return ResponseEntity.ok(habit);
    }

    /**
     * Update template visibility
     */
    @PatchMapping("/{id}/visibility")
    public ResponseEntity<HabitTemplate> updateVisibility(
            @PathVariable Long id,
            @RequestBody VisibilityRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        HabitTemplate template = templateService.updateVisibility(id, user.getId(), request.getIsPublic());
        return ResponseEntity.ok(template);
    }

    /**
     * Delete template
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        templateService.deleteTemplate(id, user.getId());
        return ResponseEntity.ok().build();
    }

    // DTO Classes
    public static class ShareTemplateRequest {
        private Boolean isPublic;

        public Boolean getIsPublic() {
            return isPublic;
        }

        public void setIsPublic(Boolean isPublic) {
            this.isPublic = isPublic;
        }
    }

    public static class VisibilityRequest {
        private Boolean isPublic;

        public Boolean getIsPublic() {
            return isPublic;
        }

        public void setIsPublic(Boolean isPublic) {
            this.isPublic = isPublic;
        }
    }
}
