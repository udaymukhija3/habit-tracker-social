package com.habittracker.repository;

import com.habittracker.model.AccountabilityPartnership;
import com.habittracker.model.PartnershipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountabilityPartnershipRepository extends JpaRepository<AccountabilityPartnership, Long> {

    // Find partnerships by user (either as user1 or user2)
    @Query("SELECT p FROM AccountabilityPartnership p WHERE p.user1.id = :userId OR p.user2.id = :userId ORDER BY p.createdAt DESC")
    Page<AccountabilityPartnership> findByUserId(@Param("userId") Long userId, Pageable pageable);

    // Find active partnerships for a user
    @Query("SELECT p FROM AccountabilityPartnership p WHERE (p.user1.id = :userId OR p.user2.id = :userId) AND p.status = 'ACTIVE'")
    List<AccountabilityPartnership> findActiveByUserId(@Param("userId") Long userId);

    // Find pending partnership requests for a user (as user2/receiver)
    @Query("SELECT p FROM AccountabilityPartnership p WHERE p.user2.id = :userId AND p.status = 'PENDING'")
    List<AccountabilityPartnership> findPendingRequestsForUser(@Param("userId") Long userId);

    // Find partnership between two users
    @Query("SELECT p FROM AccountabilityPartnership p WHERE " +
            "(p.user1.id = :userId1 AND p.user2.id = :userId2) OR " +
            "(p.user1.id = :userId2 AND p.user2.id = :userId1)")
    Optional<AccountabilityPartnership> findByUserPair(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Check if partnership exists between two users
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM AccountabilityPartnership p WHERE " +
            "(p.user1.id = :userId1 AND p.user2.id = :userId2) OR " +
            "(p.user1.id = :userId2 AND p.user2.id = :userId1)")
    boolean existsByUserPair(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Count active partnerships for a user
    @Query("SELECT COUNT(p) FROM AccountabilityPartnership p WHERE (p.user1.id = :userId OR p.user2.id = :userId) AND p.status = 'ACTIVE'")
    Long countActiveByUserId(@Param("userId") Long userId);

    // Find by status
    Page<AccountabilityPartnership> findByStatus(PartnershipStatus status, Pageable pageable);
}
