package service

import (
	"errors"

	"github.com/paperfrog5607/AuroraLauncher/backend/internal/model"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/repository"
)

type GameService struct {
	repo *repository.GameRepository
}

func NewGameService(repo *repository.GameRepository) *GameService {
	return &GameService{repo: repo}
}

func (s *GameService) Create(game *model.Game) error {
	if game.Name == "" {
		return errors.New("game name is required")
	}
	return s.repo.Create(game)
}

func (s *GameService) GetByID(id int64) (*model.Game, error) {
	return s.repo.GetByID(id)
}

func (s *GameService) List(limit, offset int) ([]*model.Game, error) {
	if limit <= 0 {
		limit = 20
	}
	if offset < 0 {
		offset = 0
	}
	return s.repo.List(limit, offset)
}

func (s *GameService) ListByUser(userID int64, limit, offset int) ([]*model.Game, error) {
	if limit <= 0 {
		limit = 20
	}
	if offset < 0 {
		offset = 0
	}
	return s.repo.ListByUser(userID, limit, offset)
}

func (s *GameService) Update(game *model.Game) error {
	if game.Name == "" {
		return errors.New("game name is required")
	}
	return s.repo.Update(game)
}

func (s *GameService) Delete(id int64) error {
	return s.repo.Delete(id)
}
