package com.habittracker.repository;

import com.habittracker.model.HabitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitTemplateRepository extends JpaRepository<HabitTemplate, Long> {

    // Find templates created by user
    Page<HabitTemplate> findByCreatorIdOrderByCreatedAtDesc(Long creatorId, Pageable pageable);

    // Find public templates (paginated)
    Page<HabitTemplate> findByIsPublicTrueOrderByUseCountDesc(Pageable pageable);

    // Find popular templates (most used)
    @Query("SELECT t FROM HabitTemplate t WHERE t.isPublic = true ORDER BY t.useCount DESC")
    Page<HabitTemplate> findPopularTemplates(Pageable pageable);

    // Find templates from friends
    @Query("SELECT t FROM HabitTemplate t WHERE t.creator.id IN :friendIds AND t.isPublic = true ORDER BY t.useCount DESC")
    Page<HabitTemplate> findTemplatesByFriends(@Param("friendIds") List<Long> friendIds, Pageable pageable);

    // Search templates by name
    @Query("SELECT t FROM HabitTemplate t WHERE t.isPublic = true AND LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<HabitTemplate> searchTemplates(@Param("query") String query, Pageable pageable);

    // Count templates by creator
    Long countByCreatorId(Long creatorId);
}
