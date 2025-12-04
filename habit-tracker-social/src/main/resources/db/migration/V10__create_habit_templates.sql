-- Create habit_template table for sharing habits
CREATE TABLE habit_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    frequency VARCHAR(50) NOT NULL,
    target_value INT,
    target_unit VARCHAR(50),
    creator_id BIGINT NOT NULL,
    use_count INT DEFAULT 0,
    is_public BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_public_popular (is_public, use_count DESC),
    INDEX idx_creator (creator_id)
);
