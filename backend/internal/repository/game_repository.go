package repository

import (
	"database/sql"
	"time"

	"github.com/paperfrog5607/AuroraLauncher/backend/internal/model"
)

type GameRepository struct {
	db *sql.DB
}

func NewGameRepository(db *sql.DB) *GameRepository {
	return &GameRepository{db: db}
}

func (r *GameRepository) Create(game *model.Game) error {
	query := `
		INSERT INTO games (name, type, platform, install_path, version, icon, cover, description, user_id, created_at, updated_at)
		VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11)
		RETURNING id
	`
	now := time.Now()
	err := r.db.QueryRow(
		query,
		game.Name,
		game.Type,
		game.Platform,
		game.InstallPath,
		game.Version,
		game.Icon,
		game.Cover,
		game.Description,
		game.UserID,
		now,
		now,
	).Scan(&game.ID)

	if err != nil {
		return err
	}

	game.CreatedAt = now
	game.UpdatedAt = now
	return nil
}

func (r *GameRepository) GetByID(id int64) (*model.Game, error) {
	query := `
		SELECT id, name, type, platform, install_path, version, icon, cover, description, user_id, created_at, updated_at
		FROM games WHERE id = $1
	`
	game := &model.Game{}
	err := r.db.QueryRow(query, id).Scan(
		&game.ID,
		&game.Name,
		&game.Type,
		&game.Platform,
		&game.InstallPath,
		&game.Version,
		&game.Icon,
		&game.Cover,
		&game.Description,
		&game.UserID,
		&game.CreatedAt,
		&game.UpdatedAt,
	)
	if err != nil {
		return nil, err
	}
	return game, nil
}

func (r *GameRepository) List(limit, offset int) ([]*model.Game, error) {
	query := `
		SELECT id, name, type, platform, install_path, version, icon, cover, description, user_id, created_at, updated_at
		FROM games ORDER BY created_at DESC LIMIT $1 OFFSET $2
	`
	rows, err := r.db.Query(query, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var games []*model.Game
	for rows.Next() {
		game := &model.Game{}
		if err := rows.Scan(
			&game.ID,
			&game.Name,
			&game.Type,
			&game.Platform,
			&game.InstallPath,
			&game.Version,
			&game.Icon,
			&game.Cover,
			&game.Description,
			&game.UserID,
			&game.CreatedAt,
			&game.UpdatedAt,
		); err != nil {
			return nil, err
		}
		games = append(games, game)
	}
	return games, nil
}

func (r *GameRepository) ListByUser(userID int64, limit, offset int) ([]*model.Game, error) {
	query := `
		SELECT id, name, type, platform, install_path, version, icon, cover, description, user_id, created_at, updated_at
		FROM games WHERE user_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3
	`
	rows, err := r.db.Query(query, userID, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var games []*model.Game
	for rows.Next() {
		game := &model.Game{}
		if err := rows.Scan(
			&game.ID,
			&game.Name,
			&game.Type,
			&game.Platform,
			&game.InstallPath,
			&game.Version,
			&game.Icon,
			&game.Cover,
			&game.Description,
			&game.UserID,
			&game.CreatedAt,
			&game.UpdatedAt,
		); err != nil {
			return nil, err
		}
		games = append(games, game)
	}
	return games, nil
}

func (r *GameRepository) Update(game *model.Game) error {
	query := `
		UPDATE games SET name = $1, type = $2, platform = $3, install_path = $4, version = $5, icon = $6, cover = $7, description = $8, updated_at = $9
		WHERE id = $10
	`
	game.UpdatedAt = time.Now()
	_, err := r.db.Exec(query, game.Name, game.Type, game.Platform, game.InstallPath, game.Version, game.Icon, game.Cover, game.Description, game.UpdatedAt, game.ID)
	return err
}

func (r *GameRepository) Delete(id int64) error {
	query := `DELETE FROM games WHERE id = $1`
	_, err := r.db.Exec(query, id)
	return err
}
