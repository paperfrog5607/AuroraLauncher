package config

import (
	"os"
)

type Config struct {
	Port        string
	DatabaseURL string
	JWTSecret   string
}

func Load() *Config {
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	dbURL := os.Getenv("DATABASE_URL")
	if dbURL == "" {
		dbURL = "postgres://postgres:postgres@localhost:5432/aurora?sslmode=disable"
	}

	jwtSecret := os.Getenv("JWT_SECRET")
	if jwtSecret == "" {
		jwtSecret = "aurora-launcher-secret-key-change-in-production"
	}

	return &Config{
		Port:        port,
		DatabaseURL: dbURL,
		JWTSecret:   jwtSecret,
	}
}
