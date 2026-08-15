-- Phase 9: Gamification Badges, Universal Bookmarks, and In-App Notifications

CREATE TABLE IF NOT EXISTS student_badges (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    badge_code VARCHAR(100) NOT NULL,
    badge_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    icon_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    earned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_badge_user FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_student_badge UNIQUE (student_id, badge_code)
);

CREATE INDEX IF NOT EXISTS idx_student_badges_student_id ON student_badges(student_id);

CREATE TABLE IF NOT EXISTS student_bookmarks (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    item_type VARCHAR(50) NOT NULL,
    item_id BIGINT NOT NULL,
    item_title VARCHAR(255) NOT NULL,
    item_subtitle VARCHAR(255),
    item_route VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_bookmark_user FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_student_bookmark UNIQUE (student_id, item_type, item_id)
);

CREATE INDEX IF NOT EXISTS idx_student_bookmarks_student_id ON student_bookmarks(student_id);
CREATE INDEX IF NOT EXISTS idx_student_bookmarks_type ON student_bookmarks(item_type);

CREATE TABLE IF NOT EXISTS student_notifications (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    action_route VARCHAR(255),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_notification_user FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_student_notifications_student_id ON student_notifications(student_id);
CREATE INDEX IF NOT EXISTS idx_student_notifications_read ON student_notifications(student_id, is_read);
