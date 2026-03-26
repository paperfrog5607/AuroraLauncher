package repository

import (
	"database/sql"
	"time"

	"github.com/paperfrog5607/AuroraLauncher/backend/internal/model"
)

type ModRepository struct {
	db *sql.DB
}

func NewModRepository(db *sql.DB) *ModRepository {
	return &ModRepository{db: db}
}

func (r *ModRepository) Create(mod *model.Mod) error {
	query := `
		INSERT INTO mods (name, version, author, description, download_url, image_url, game_id, user_id, created_at, updated_at)
		VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
		RETURNING id
	`
	now := time.Now()
	err := r.db.QueryRow(
		query,
		mod.Name,
		mod.Version,
		mod.Author,
		mod.Description,
		mod.DownloadURL,
		mod.ImageURL,
		mod.GameID,
		mod.UserID,
		now,
		now,
	).Scan(&mod.ID)

	if err != nil {
		return err
	}

	mod.CreatedAt = now
	mod.UpdatedAt = now
	return nil
}

func (r *ModRepository) GetByID(id int64) (*model.Mod, error) {
	query := `
		SELECT id, name, version, author, description, download_url, image_url, game_id, user_id, created_at, updated_at
		FROM mods WHERE id = $1
	`
	mod := &model.Mod{}
	err := r.db.QueryRow(query, id).Scan(
		&mod.ID,
		&mod.Name,
		&mod.Version,
		&mod.Author,
		&mod.Description,
		&mod.DownloadURL,
		&mod.ImageURL,
		&mod.GameID,
		&mod.UserID,
		&mod.CreatedAt,
		&mod.UpdatedAt,
	)
	if err != nil {
		return nil, err
	}
	return mod, nil
}

func (r *ModRepository) List(limit, offset int) ([]*model.Mod, error) {
	query := `
		SELECT id, name, version, author, description, download_url, image_url, game_id, user_id, created_at, updated_at
		FROM mods ORDER BY created_at DESC LIMIT $1 OFFSET $2
	`
	rows, err := r.db.Query(query, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var mods []*model.Mod
	for rows.Next() {
		mod := &model.Mod{}
		if err := rows.Scan(
			&mod.ID,
			&mod.Name,
			&mod.Version,
			&mod.Author,
			&mod.Description,
			&mod.DownloadURL,
			&mod.ImageURL,
			&mod.GameID,
			&mod.UserID,
			&mod.CreatedAt,
			&mod.UpdatedAt,
		); err != nil {
			return nil, err
		}
		mods = append(mods, mod)
	}
	return mods, nil
}

func (r *ModRepository) ListByGame(gameID int64, limit, offset int) ([]*model.Mod, error) {
	query := `
		SELECT id, name, version, author, description, download_url, image_url, game_id, user_id, created_at, updated_at
		FROM mods WHERE game_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3
	`
	rows, err := r.db.Query(query, gameID, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var mods []*model.Mod
	for rows.Next() {
		mod := &model.Mod{}
		if err := rows.Scan(
			&mod.ID,
			&mod.Name,
			&mod.Version,
			&mod.Author,
			&mod.Description,
			&mod.DownloadURL,
			&mod.ImageURL,
			&mod.GameID,
			&mod.UserID,
			&mod.CreatedAt,
			&mod.UpdatedAt,
		); err != nil {
			return nil, err
		}
		mods = append(mods, mod)
	}
	return mods, nil
}

func (r *ModRepository) Update(mod *model.Mod) error {
	query := `
		UPDATE mods SET name = $1, version = $2, author = $3, description = $4, download_url = $5, image_url = $6, updated_at = $7
		WHERE id = $8
	`
	mod.UpdatedAt = time.Now()
	_, err := r.db.Exec(query, mod.Name, mod.Version, mod.Author, mod.Description, mod.DownloadURL, mod.ImageURL, mod.UpdatedAt, mod.ID)
	return err
}

func (r *ModRepository) Delete(id int64) error {
	query := `DELETE FROM mods WHERE id = $1`
	_, err := r.db.Exec(query, id)
	return err
}
