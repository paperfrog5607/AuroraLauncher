-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar TEXT DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create games table
CREATE TABLE IF NOT EXISTS games (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) DEFAULT 'standard',
    platform VARCHAR(50) DEFAULT 'steam',
    install_path TEXT DEFAULT '',
    version VARCHAR(50) DEFAULT '',
    icon TEXT DEFAULT '',
    cover TEXT DEFAULT '',
    description TEXT DEFAULT '',
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create mods table
CREATE TABLE IF NOT EXISTS mods (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    version VARCHAR(50) DEFAULT '',
    author VARCHAR(255) DEFAULT '',
    description TEXT DEFAULT '',
    download_url TEXT DEFAULT '',
    image_url TEXT DEFAULT '',
    game_id BIGINT REFERENCES games(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create modpacks table
CREATE TABLE IF NOT EXISTS modpacks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    version VARCHAR(50) DEFAULT '',
    author VARCHAR(255) DEFAULT '',
    description TEXT DEFAULT '',
    download_url TEXT DEFAULT '',
    image_url TEXT DEFAULT '',
    minecraft_version VARCHAR(50) DEFAULT '',
    game_id BIGINT REFERENCES games(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create saves table
CREATE TABLE IF NOT EXISTS saves (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    path TEXT NOT NULL,
    game_id BIGINT REFERENCES games(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create ratings table
CREATE TABLE IF NOT EXISTS ratings (
    id BIGSERIAL PRIMARY KEY,
    score INTEGER CHECK (score >= 1 AND score <= 5),
    comment TEXT DEFAULT '',
    target_id BIGINT NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create follows table
CREATE TABLE IF NOT EXISTS follows (
    id BIGSERIAL PRIMARY KEY,
    follower_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    following_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(follower_id, following_id)
);

-- Create shares table
CREATE TABLE IF NOT EXISTS shares (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT DEFAULT '',
    type VARCHAR(50) DEFAULT 'post',
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_games_user_id ON games(user_id);
CREATE INDEX IF NOT EXISTS idx_mods_game_id ON mods(game_id);
CREATE INDEX IF NOT EXISTS idx_mods_user_id ON mods(user_id);
CREATE INDEX IF NOT EXISTS idx_modpacks_game_id ON modpacks(game_id);
CREATE INDEX IF NOT EXISTS idx_modpacks_user_id ON modpacks(user_id);
CREATE INDEX IF NOT EXISTS idx_saves_game_id ON saves(game_id);
CREATE INDEX IF NOT EXISTS idx_saves_user_id ON saves(user_id);
CREATE INDEX IF NOT EXISTS idx_ratings_target ON ratings(target_id, target_type);
CREATE INDEX IF NOT EXISTS idx_follows_follower ON follows(follower_id);
CREATE INDEX IF NOT EXISTS idx_follows_following ON follows(following_id);
CREATE INDEX IF NOT EXISTS idx_shares_user_id ON shares(user_id);