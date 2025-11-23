-- Habit Tracker Social - Initial Database Schema
-- Version: 1.0
-- Description: Creates all tables for the application
-- Author: Flyway Migration
-- Date: 2025-11-21

-- ============================================
-- Table: users
-- Description: User accounts and authentication
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at DATETIME(6),
    last_login_at DATETIME(6),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table: habits
-- Description: User habits and their configuration
-- ============================================
CREATE TABLE IF NOT EXISTS habits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(50) NOT NULL,
    frequency VARCHAR(20) NOT NULL,
    target_value INT NOT NULL,
    target_unit VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_habits_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table: habit_completions
-- Description: Records of habit completions
-- ============================================
CREATE TABLE IF NOT EXISTS habit_completions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    habit_id BIGINT NOT NULL,
    completion_date DATETIME(6) NOT NULL,
    completion_value INT,
    notes TEXT,
    created_at DATETIME(6),
    CONSTRAINT fk_completions_habit FOREIGN KEY (habit_id) REFERENCES habits(id) ON DELETE CASCADE,
    INDEX idx_habit_id (habit_id),
    INDEX idx_completion_date (completion_date),
    INDEX idx_habit_date (habit_id, completion_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table: streaks
-- Description: Habit streak tracking
-- ============================================
CREATE TABLE IF NOT EXISTS streaks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    habit_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    current_streak INT NOT NULL DEFAULT 0,
    longest_streak INT NOT NULL DEFAULT 0,
    last_completion_date DATETIME(6),
    streak_start_date DATETIME(6),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_streaks_habit FOREIGN KEY (habit_id) REFERENCES habits(id) ON DELETE CASCADE,
    CONSTRAINT fk_streaks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_habit_id (habit_id),
    INDEX idx_user_id (user_id),
    INDEX idx_current_streak (current_streak),
    UNIQUE KEY uk_habit_user (habit_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table: friendships
-- Description: User friendship relationships
-- ============================================
CREATE TABLE IF NOT EXISTS friendships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_id BIGINT NOT NULL,
    addressee_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_friendships_requester FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friendships_addressee FOREIGN KEY (addressee_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_requester_id (requester_id),
    INDEX idx_addressee_id (addressee_id),
    INDEX idx_status (status),
    UNIQUE KEY uk_friendship (requester_id, addressee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table: user_friends (Many-to-Many Join Table)
-- Description: Direct friend relationships after acceptance
-- ============================================
CREATE TABLE IF NOT EXISTS user_friends (
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_user_friends_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_friends_friend FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_friend_id (friend_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table: competitions
-- Description: Habit competitions between users
-- ============================================
CREATE TABLE IF NOT EXISTS competitions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    creator_id BIGINT NOT NULL,
    habit_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    start_date DATETIME(6) NOT NULL,
    end_date DATETIME(6) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6),
    CONSTRAINT fk_competitions_creator FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_competitions_habit FOREIGN KEY (habit_id) REFERENCES habits(id) ON DELETE CASCADE,
    INDEX idx_creator_id (creator_id),
    INDEX idx_habit_id (habit_id),
    INDEX idx_type (type),
    INDEX idx_is_active (is_active),
    INDEX idx_dates (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table: competition_participants
-- Description: Users participating in competitions
-- ============================================
CREATE TABLE IF NOT EXISTS competition_participants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    competition_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    score INT NOT NULL DEFAULT 0,
    rank INT NOT NULL DEFAULT 0,
    joined_at DATETIME(6),
    CONSTRAINT fk_participants_competition FOREIGN KEY (competition_id) REFERENCES competitions(id) ON DELETE CASCADE,
    CONSTRAINT fk_participants_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_competition_id (competition_id),
    INDEX idx_user_id (user_id),
    INDEX idx_score (score DESC),
    INDEX idx_rank (rank),
    UNIQUE KEY uk_competition_user (competition_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table: notifications
-- Description: User notifications
-- ============================================
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'UNREAD',
    created_at DATETIME(6),
    read_at DATETIME(6),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_user_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Initial Data (Optional)
-- ============================================

-- Add any seed data here if needed for development
-- Example:
-- INSERT INTO users (username, email, password, role, created_at, enabled)
-- VALUES ('admin', 'admin@habittracker.com', '$2a$10$...', 'ADMIN', NOW(), TRUE);
