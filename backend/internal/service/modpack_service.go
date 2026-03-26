package service

import (
	"errors"

	"github.com/paperfrog5607/AuroraLauncher/backend/internal/model"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/repository"
)

type ModpackService struct {
	repo *repository.ModpackRepository
}

func NewModpackService(repo *repository.ModpackRepository) *ModpackService {
	return &ModpackService{repo: repo}
}

func (s *ModpackService) Create(modpack *model.Modpack) error {
	if modpack.Name == "" {
		return errors.New("modpack name is required")
	}
	return s.repo.Create(modpack)
}

func (s *ModpackService) GetByID(id int64) (*model.Modpack, error) {
	return s.repo.GetByID(id)
}

func (s *ModpackService) List(limit, offset int) ([]*model.Modpack, error) {
	if limit <= 0 {
		limit = 20
	}
	if offset < 0 {
		offset = 0
	}
	return s.repo.List(limit, offset)
}

func (s *ModpackService) ListByGame(gameID int64, limit, offset int) ([]*model.Modpack, error) {
	if limit <= 0 {
		limit = 20
	}
	if offset < 0 {
		offset = 0
	}
	return s.repo.ListByGame(gameID, limit, offset)
}

func (s *ModpackService) Update(modpack *model.Modpack) error {
	if modpack.Name == "" {
		return errors.New("modpack name is required")
	}
	return s.repo.Update(modpack)
}

func (s *ModpackService) Delete(id int64) error {
	return s.repo.Delete(id)
}
