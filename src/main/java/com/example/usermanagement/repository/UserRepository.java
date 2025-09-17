package com.example.usermanagement.repository;

import com.example.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Basic authentication queries
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // Validation queries
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Find active users only
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActiveUsers();

    // Find users by role
    @Query("SELECT u FROM User u WHERE u.userRole = :role AND u.isActive = true")
    List<User> findActiveUsersByRole(@Param("role") User.UserRole role);

    // Search by name
    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:name% AND u.isActive = true")
    List<User> findActiveUsersByNameContaining(@Param("name") String name);

    // Count active users
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();
}
