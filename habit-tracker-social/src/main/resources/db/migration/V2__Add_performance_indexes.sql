-- Habit Tracker Social - Performance Indexes
-- Version: 2.0
-- Description: Adds additional indexes for query performance optimization
-- Author: Flyway Migration
-- Date: 2025-11-21

-- ============================================
-- Composite Indexes for Common Queries
-- ============================================

-- Optimize habit queries by user and status
CREATE INDEX idx_habits_user_active ON habits(user_id, is_active);

-- Optimize habit queries by user and type
CREATE INDEX idx_habits_user_type ON habits(user_id, type);

-- Optimize completion queries by date range
CREATE INDEX idx_completions_habit_date_range ON habit_completions(habit_id, completion_date DESC);

-- Optimize streak queries for leaderboards
CREATE INDEX idx_streaks_longest ON streaks(longest_streak DESC);

-- Optimize notification queries for unread count
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, status, created_at DESC);

-- Optimize friendship queries for pending requests
CREATE INDEX idx_friendships_addressee_status ON friendships(addressee_id, status);
CREATE INDEX idx_friendships_requester_status ON friendships(requester_id, status);

-- Optimize competition participant queries for leaderboards
CREATE INDEX idx_participants_comp_score ON competition_participants(competition_id, score DESC);

-- Optimize active competition queries
CREATE INDEX idx_competitions_active_dates ON competitions(is_active, start_date, end_date);

-- ============================================
-- Full-Text Search Indexes (for future use)
-- ============================================

-- Full-text search on habit names and descriptions
-- ALTER TABLE habits ADD FULLTEXT INDEX ft_idx_habits_search (name, description);

-- Full-text search on competition names and descriptions
-- ALTER TABLE competitions ADD FULLTEXT INDEX ft_idx_competitions_search (name, description);

-- ============================================
-- Performance Statistics Update
-- ============================================

-- Force MySQL to analyze the tables for better query optimization
ANALYZE TABLE users;
ANALYZE TABLE habits;
ANALYZE TABLE habit_completions;
ANALYZE TABLE streaks;
ANALYZE TABLE friendships;
ANALYZE TABLE user_friends;
ANALYZE TABLE competitions;
ANALYZE TABLE competition_participants;
ANALYZE TABLE notifications;
