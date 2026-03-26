package service

import (
	"errors"

	"github.com/paperfrog5607/AuroraLauncher/backend/internal/model"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/repository"
)

type ModService struct {
	repo *repository.ModRepository
}

func NewModService(repo *repository.ModRepository) *ModService {
	return &ModService{repo: repo}
}

func (s *ModService) Create(mod *model.Mod) error {
	if mod.Name == "" {
		return errors.New("mod name is required")
	}
	return s.repo.Create(mod)
}

func (s *ModService) GetByID(id int64) (*model.Mod, error) {
	return s.repo.GetByID(id)
}

func (s *ModService) List(limit, offset int) ([]*model.Mod, error) {
	if limit <= 0 {
		limit = 20
	}
	if offset < 0 {
		offset = 0
	}
	return s.repo.List(limit, offset)
}

func (s *ModService) ListByGame(gameID int64, limit, offset int) ([]*model.Mod, error) {
	if limit <= 0 {
		limit = 20
	}
	if offset < 0 {
		offset = 0
	}
	return s.repo.ListByGame(gameID, limit, offset)
}

func (s *ModService) Update(mod *model.Mod) error {
	if mod.Name == "" {
		return errors.New("mod name is required")
	}
	return s.repo.Update(mod)
}

func (s *ModService) Delete(id int64) error {
	return s.repo.Delete(id)
}
