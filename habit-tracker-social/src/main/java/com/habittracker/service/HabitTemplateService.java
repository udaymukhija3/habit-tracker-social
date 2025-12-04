package com.habittracker.service;

import com.habittracker.model.*;
import com.habittracker.repository.HabitRepository;
import com.habittracker.repository.HabitTemplateRepository;
import com.habittracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Habit Template Service
 * 
 * Manages habit templates for viral growth and easier onboarding
 */
@Service
@Transactional
public class HabitTemplateService {

    @Autowired
    private HabitTemplateRepository templateRepository;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityFeedService activityFeedService;

    /**
     * Create a template from existing habit
     */
    public HabitTemplate shareHabitAsTemplate(Long habitId, User creator, boolean isPublic) {
        Optional<Habit> habitOpt = habitRepository.findById(habitId);
        if (habitOpt.isEmpty()) {
            throw new RuntimeException("Habit not found");
        }

        Habit habit = habitOpt.get();

        // Verify ownership
        if (!habit.getUser().getId().equals(creator.getId())) {
            throw new RuntimeException("Not authorized to share this habit");
        }

        HabitTemplate template = new HabitTemplate(
                habit.getName(),
                creator,
                habit.getType(),
                habit.getFrequency());
        template.setDescription(habit.getDescription());
        template.setTargetValue(habit.getTargetValue());
        template.setTargetUnit(habit.getTargetUnit());
        template.setIsPublic(isPublic);

        HabitTemplate saved = templateRepository.save(template);

        // Create activity
        if (isPublic) {
            activityFeedService.createActivity(creator, ActivityType.HABIT_SHARED,
                    creator.getUsername() + " shared template: " + template.getName());
        }

        return saved;
    }

    /**
     * Clone a template to create a new habit
     */
    public Habit cloneTemplate(Long templateId, User user) {
        Optional<HabitTemplate> templateOpt = templateRepository.findById(templateId);
        if (templateOpt.isEmpty()) {
            throw new RuntimeException("Template not found");
        }

        HabitTemplate template = templateOpt.get();

        // Increment use count
        template.incrementUseCount();
        templateRepository.save(template);

        // Create new habit from template
        Habit habit = new Habit();
        habit.setName(template.getName());
        habit.setDescription(template.getDescription());
        habit.setType(template.getType());
        habit.setFrequency(template.getFrequency());
        habit.setTargetValue(template.getTargetValue());
        habit.setTargetUnit(template.getTargetUnit());
        habit.setUser(user);
        habit.setActive(true);

        return habitRepository.save(habit);
    }

    /**
     * Get template by ID
     */
    public Optional<HabitTemplate> findById(Long id) {
        return templateRepository.findById(id);
    }

    /**
     * Get popular templates (public, most used)
     */
    public Page<HabitTemplate> getPopularTemplates(Pageable pageable) {
        return templateRepository.findPopularTemplates(pageable);
    }

    /**
     * Get templates from friends
     */
    public Page<HabitTemplate> getTemplatesFromFriends(Long userId, Pageable pageable) {
        List<User> friends = userRepository.findFriendsByUserId(userId);
        List<Long> friendIds = friends.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        if (friendIds.isEmpty()) {
            return Page.empty(pageable);
        }

        return templateRepository.findTemplatesByFriends(friendIds, pageable);
    }

    /**
     * Get user's templates
     */
    public Page<HabitTemplate> getUserTemplates(Long userId, Pageable pageable) {
        return templateRepository.findByCreatorIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Search templates
     */
    public Page<HabitTemplate> searchTemplates(String query, Pageable pageable) {
        return templateRepository.searchTemplates(query, pageable);
    }

    /**
     * Update template visibility
     */
    public HabitTemplate updateVisibility(Long templateId, Long userId, boolean isPublic) {
        Optional<HabitTemplate> templateOpt = templateRepository.findById(templateId);
        if (templateOpt.isEmpty()) {
            throw new RuntimeException("Template not found");
        }

        HabitTemplate template = templateOpt.get();

        // Verify ownership
        if (!template.getCreator().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this template");
        }

        template.setIsPublic(isPublic);
        return templateRepository.save(template);
    }

    /**
     * Delete template
     */
    public void deleteTemplate(Long templateId, Long userId) {
        Optional<HabitTemplate> templateOpt = templateRepository.findById(templateId);
        if (templateOpt.isEmpty()) {
            throw new RuntimeException("Template not found");
        }

        HabitTemplate template = templateOpt.get();

        // Verify ownership
        if (!template.getCreator().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this template");
        }

        templateRepository.delete(template);
    }
}
