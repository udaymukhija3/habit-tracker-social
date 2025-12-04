-- Create accountability_partnership table for 1-on-1 accountability
CREATE TABLE accountability_partnership (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    shared_goal TEXT,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(50) DEFAULT 'PENDING',
    user1_completions INT DEFAULT 0,
    user2_completions INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_partnership (user1_id, user2_id),
    INDEX idx_status (status),
    INDEX idx_users (user1_id, user2_id)
);
