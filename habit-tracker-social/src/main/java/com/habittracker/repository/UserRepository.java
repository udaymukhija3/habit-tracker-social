package com.habittracker.repository;

import com.habittracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username% OR u.firstName LIKE %:username% OR u.lastName LIKE %:username%")
    List<User> findByUsernameContainingIgnoreCase(@Param("username") String username);
    
    @Query("SELECT u FROM User u WHERE u.id IN (SELECT f.addressee.id FROM Friendship f WHERE f.requester.id = :userId AND f.status = 'ACCEPTED')")
    List<User> findFriendsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT u FROM User u WHERE u.id IN (SELECT f.requester.id FROM Friendship f WHERE f.addressee.id = :userId AND f.status = 'ACCEPTED')")
    List<User> findFriendsOfByUserId(@Param("userId") Long userId);
}
