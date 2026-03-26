package repository

import (
	"database/sql"
	"time"

	"github.com/paperfrog5607/AuroraLauncher/backend/internal/model"
)

type ModpackRepository struct {
	db *sql.DB
}

func NewModpackRepository(db *sql.DB) *ModpackRepository {
	return &ModpackRepository{db: db}
}

func (r *ModpackRepository) Create(modpack *model.Modpack) error {
	query := `
		INSERT INTO modpacks (name, version, author, description, download_url, image_url, minecraft_version, game_id, user_id, created_at, updated_at)
		VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11)
		RETURNING id
	`
	now := time.Now()
	err := r.db.QueryRow(
		query,
		modpack.Name,
		modpack.Version,
		modpack.Author,
		modpack.Description,
		modpack.DownloadURL,
		modpack.ImageURL,
		modpack.MinecraftVersion,
		modpack.GameID,
		modpack.UserID,
		now,
		now,
	).Scan(&modpack.ID)

	if err != nil {
		return err
	}

	modpack.CreatedAt = now
	modpack.UpdatedAt = now
	return nil
}

func (r *ModpackRepository) GetByID(id int64) (*model.Modpack, error) {
	query := `
		SELECT id, name, version, author, description, download_url, image_url, minecraft_version, game_id, user_id, created_at, updated_at
		FROM modpacks WHERE id = $1
	`
	modpack := &model.Modpack{}
	err := r.db.QueryRow(query, id).Scan(
		&modpack.ID,
		&modpack.Name,
		&modpack.Version,
		&modpack.Author,
		&modpack.Description,
		&modpack.DownloadURL,
		&modpack.ImageURL,
		&modpack.MinecraftVersion,
		&modpack.GameID,
		&modpack.UserID,
		&modpack.CreatedAt,
		&modpack.UpdatedAt,
	)
	if err != nil {
		return nil, err
	}
	return modpack, nil
}

func (r *ModpackRepository) List(limit, offset int) ([]*model.Modpack, error) {
	query := `
		SELECT id, name, version, author, description, download_url, image_url, minecraft_version, game_id, user_id, created_at, updated_at
		FROM modpacks ORDER BY created_at DESC LIMIT $1 OFFSET $2
	`
	rows, err := r.db.Query(query, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var modpacks []*model.Modpack
	for rows.Next() {
		modpack := &model.Modpack{}
		if err := rows.Scan(
			&modpack.ID,
			&modpack.Name,
			&modpack.Version,
			&modpack.Author,
			&modpack.Description,
			&modpack.DownloadURL,
			&modpack.ImageURL,
			&modpack.MinecraftVersion,
			&modpack.GameID,
			&modpack.UserID,
			&modpack.CreatedAt,
			&modpack.UpdatedAt,
		); err != nil {
			return nil, err
		}
		modpacks = append(modpacks, modpack)
	}
	return modpacks, nil
}

func (r *ModpackRepository) ListByGame(gameID int64, limit, offset int) ([]*model.Modpack, error) {
	query := `
		SELECT id, name, version, author, description, download_url, image_url, minecraft_version, game_id, user_id, created_at, updated_at
		FROM modpacks WHERE game_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3
	`
	rows, err := r.db.Query(query, gameID, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var modpacks []*model.Modpack
	for rows.Next() {
		modpack := &model.Modpack{}
		if err := rows.Scan(
			&modpack.ID,
			&modpack.Name,
			&modpack.Version,
			&modpack.Author,
			&modpack.Description,
			&modpack.DownloadURL,
			&modpack.ImageURL,
			&modpack.MinecraftVersion,
			&modpack.GameID,
			&modpack.UserID,
			&modpack.CreatedAt,
			&modpack.UpdatedAt,
		); err != nil {
			return nil, err
		}
		modpacks = append(modpacks, modpack)
	}
	return modpacks, nil
}

func (r *ModpackRepository) Update(modpack *model.Modpack) error {
	query := `
		UPDATE modpacks SET name = $1, version = $2, author = $3, description = $4, download_url = $5, image_url = $6, minecraft_version = $7, updated_at = $8
		WHERE id = $9
	`
	modpack.UpdatedAt = time.Now()
	_, err := r.db.Exec(query, modpack.Name, modpack.Version, modpack.Author, modpack.Description, modpack.DownloadURL, modpack.ImageURL, modpack.MinecraftVersion, modpack.UpdatedAt, modpack.ID)
	return err
}

func (r *ModpackRepository) Delete(id int64) error {
	query := `DELETE FROM modpacks WHERE id = $1`
	_, err := r.db.Exec(query, id)
	return err
}
