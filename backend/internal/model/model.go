package model

import (
	"time"
)

type User struct {
	ID        int64     `json:"id" db:"id"`
	Username  string    `json:"username" db:"username"`
	Email     string    `json:"email" db:"email"`
	Password  string    `json:"-" db:"password"`
	Avatar    string    `json:"avatar" db:"avatar"`
	CreatedAt time.Time `json:"created_at" db:"created_at"`
	UpdatedAt time.Time `json:"updated_at" db:"updated_at"`
}

type Game struct {
	ID          int64     `json:"id" db:"id"`
	Name        string    `json:"name" db:"name"`
	Type        string    `json:"type" db:"type"`
	Platform    string    `json:"platform" db:"platform"`
	InstallPath string    `json:"install_path" db:"install_path"`
	Version     string    `json:"version" db:"version"`
	Icon        string    `json:"icon" db:"icon"`
	Cover       string    `json:"cover" db:"cover"`
	Description string    `json:"description" db:"description"`
	UserID      int64     `json:"user_id" db:"user_id"`
	CreatedAt   time.Time `json:"created_at" db:"created_at"`
	UpdatedAt   time.Time `json:"updated_at" db:"updated_at"`
}

type Mod struct {
	ID          int64     `json:"id" db:"id"`
	Name        string    `json:"name" db:"name"`
	Version     string    `json:"version" db:"version"`
	Author      string    `json:"author" db:"author"`
	Description string    `json:"description" db:"description"`
	DownloadURL string    `json:"download_url" db:"download_url"`
	ImageURL    string    `json:"image_url" db:"image_url"`
	GameID      int64     `json:"game_id" db:"game_id"`
	UserID      int64     `json:"user_id" db:"user_id"`
	CreatedAt   time.Time `json:"created_at" db:"created_at"`
	UpdatedAt   time.Time `json:"updated_at" db:"updated_at"`
}

type Modpack struct {
	ID               int64     `json:"id" db:"id"`
	Name             string    `json:"name" db:"name"`
	Version          string    `json:"version" db:"version"`
	Author           string    `json:"author" db:"author"`
	Description      string    `json:"description" db:"description"`
	DownloadURL      string    `json:"download_url" db:"download_url"`
	ImageURL         string    `json:"image_url" db:"image_url"`
	MinecraftVersion string    `json:"minecraft_version" db:"minecraft_version"`
	GameID           int64     `json:"game_id" db:"game_id"`
	UserID           int64     `json:"user_id" db:"user_id"`
	CreatedAt        time.Time `json:"created_at" db:"created_at"`
	UpdatedAt        time.Time `json:"updated_at" db:"updated_at"`
}

type Save struct {
	ID        int64     `json:"id" db:"id"`
	Name      string    `json:"name" db:"name"`
	Path      string    `json:"path" db:"path"`
	GameID    int64     `json:"game_id" db:"game_id"`
	UserID    int64     `json:"user_id" db:"user_id"`
	CreatedAt time.Time `json:"created_at" db:"created_at"`
	UpdatedAt time.Time `json:"updated_at" db:"updated_at"`
}

type Rating struct {
	ID         int64     `json:"id" db:"id"`
	Score      int       `json:"score" db:"score"`
	Comment    string    `json:"comment" db:"comment"`
	TargetID   int64     `json:"target_id" db:"target_id"`
	TargetType string    `json:"target_type" db:"target_type"`
	UserID     int64     `json:"user_id" db:"user_id"`
	CreatedAt  time.Time `json:"created_at" db:"created_at"`
	UpdatedAt  time.Time `json:"updated_at" db:"updated_at"`
}

type Follow struct {
	ID          int64     `json:"id" db:"id"`
	FollowerID  int64     `json:"follower_id" db:"follower_id"`
	FollowingID int64     `json:"following_id" db:"following_id"`
	CreatedAt   time.Time `json:"created_at" db:"created_at"`
}

type Share struct {
	ID        int64     `json:"id" db:"id"`
	Title     string    `json:"title" db:"title"`
	Content   string    `json:"content" db:"content"`
	Type      string    `json:"type" db:"type"`
	UserID    int64     `json:"user_id" db:"user_id"`
	CreatedAt time.Time `json:"created_at" db:"created_at"`
	UpdatedAt time.Time `json:"updated_at" db:"updated_at"`
}
