CREATE TABLE user_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL,
    nickname VARCHAR(64) NOT NULL,
    avatar VARCHAR(255),
    phone VARCHAR(32),
    openid VARCHAR(64),
    password VARCHAR(128),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_username (username)
);

-- password 存储 BCrypt 哈希值，不再存明文。

CREATE TABLE food (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    pool_type VARCHAR(16) NOT NULL DEFAULT 'PUBLIC',
    name VARCHAR(64) NOT NULL,
    category VARCHAR(32) NOT NULL,
    image_url VARCHAR(255),
    price_range VARCHAR(32),
    tags VARCHAR(255),
    enabled TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- pool_type 字段保留做兼容，当前前端不再暴露公共/私有池切换。

CREATE TABLE food_draw_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    food_name VARCHAR(64) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE check_in (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    check_date DATE NOT NULL,
    check_time DATETIME NOT NULL,
    target_time TIME,
    status VARCHAR(16) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_date (user_id, check_date)
);

CREATE TABLE user_setting (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    wake_target_time TIME,
    notification_enabled TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_setting (user_id)
);
