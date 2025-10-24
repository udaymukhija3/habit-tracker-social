package com.habittracker.repository;

import com.habittracker.model.Friendship;
import com.habittracker.model.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    
    Optional<Friendship> findByRequesterIdAndAddresseeId(Long requesterId, Long addresseeId);
    
    List<Friendship> findByRequesterIdAndStatus(Long requesterId, FriendshipStatus status);
    
    List<Friendship> findByAddresseeIdAndStatus(Long addresseeId, FriendshipStatus status);
    
    @Query("SELECT f FROM Friendship f WHERE (f.requester.id = :userId OR f.addressee.id = :userId) AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedFriendshipsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT f FROM Friendship f WHERE f.addressee.id = :userId AND f.status = 'PENDING'")
    List<Friendship> findPendingFriendRequestsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT f FROM Friendship f WHERE f.requester.id = :userId AND f.status = 'PENDING'")
    List<Friendship> findSentFriendRequestsByUserId(@Param("userId") Long userId);
    
    boolean existsByRequesterIdAndAddresseeIdAndStatus(Long requesterId, Long addresseeId, FriendshipStatus status);
}
