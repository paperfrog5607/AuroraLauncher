package repository

import (
	"database/sql"
	"time"

	"github.com/paperfrog5607/AuroraLauncher/backend/internal/model"
)

type UserRepository struct {
	db *sql.DB
}

func NewUserRepository(db *sql.DB) *UserRepository {
	return &UserRepository{db: db}
}

func (r *UserRepository) Create(user *model.User) error {
	query := `
		INSERT INTO users (username, email, password, avatar, created_at, updated_at)
		VALUES ($1, $2, $3, $4, $5, $6)
		RETURNING id
	`
	now := time.Now()
	err := r.db.QueryRow(
		query,
		user.Username,
		user.Email,
		user.Password,
		user.Avatar,
		now,
		now,
	).Scan(&user.ID)

	if err != nil {
		return err
	}

	user.CreatedAt = now
	user.UpdatedAt = now
	return nil
}

func (r *UserRepository) GetByID(id int64) (*model.User, error) {
	query := `
		SELECT id, username, email, password, avatar, created_at, updated_at
		FROM users WHERE id = $1
	`
	user := &model.User{}
	err := r.db.QueryRow(query, id).Scan(
		&user.ID,
		&user.Username,
		&user.Email,
		&user.Password,
		&user.Avatar,
		&user.CreatedAt,
		&user.UpdatedAt,
	)
	if err != nil {
		return nil, err
	}
	return user, nil
}

func (r *UserRepository) GetByEmail(email string) (*model.User, error) {
	query := `
		SELECT id, username, email, password, avatar, created_at, updated_at
		FROM users WHERE email = $1
	`
	user := &model.User{}
	err := r.db.QueryRow(query, email).Scan(
		&user.ID,
		&user.Username,
		&user.Email,
		&user.Password,
		&user.Avatar,
		&user.CreatedAt,
		&user.UpdatedAt,
	)
	if err != nil {
		return nil, err
	}
	return user, nil
}

func (r *UserRepository) GetByUsername(username string) (*model.User, error) {
	query := `
		SELECT id, username, email, password, avatar, created_at, updated_at
		FROM users WHERE username = $1
	`
	user := &model.User{}
	err := r.db.QueryRow(query, username).Scan(
		&user.ID,
		&user.Username,
		&user.Email,
		&user.Password,
		&user.Avatar,
		&user.CreatedAt,
		&user.UpdatedAt,
	)
	if err != nil {
		return nil, err
	}
	return user, nil
}

func (r *UserRepository) List(limit, offset int) ([]*model.User, error) {
	query := `
		SELECT id, username, email, password, avatar, created_at, updated_at
		FROM users ORDER BY created_at DESC LIMIT $1 OFFSET $2
	`
	rows, err := r.db.Query(query, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var users []*model.User
	for rows.Next() {
		user := &model.User{}
		if err := rows.Scan(
			&user.ID,
			&user.Username,
			&user.Email,
			&user.Password,
			&user.Avatar,
			&user.CreatedAt,
			&user.UpdatedAt,
		); err != nil {
			return nil, err
		}
		users = append(users, user)
	}
	return users, nil
}

func (r *UserRepository) Update(user *model.User) error {
	query := `
		UPDATE users SET username = $1, email = $2, avatar = $3, updated_at = $4
		WHERE id = $5
	`
	user.UpdatedAt = time.Now()
	_, err := r.db.Exec(query, user.Username, user.Email, user.Avatar, user.UpdatedAt, user.ID)
	return err
}

func (r *UserRepository) Delete(id int64) error {
	query := `DELETE FROM users WHERE id = $1`
	_, err := r.db.Exec(query, id)
	return err
}
