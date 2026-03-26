package service

import (
	"database/sql"
	"errors"
	"time"

	"github.com/paperfrog5607/AuroraLauncher/backend/internal/model"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/repository"
	"golang.org/x/crypto/bcrypt"
)

type UserService struct {
	repo *repository.UserRepository
}

func NewUserService(repo *repository.UserRepository) *UserService {
	return &UserService{repo: repo}
}

func (s *UserService) Register(username, email, password string) (*model.User, error) {
	existingUser, err := s.repo.GetByEmail(email)
	if err == nil && existingUser != nil {
		return nil, errors.New("email already registered")
	}

	existingUser, err = s.repo.GetByUsername(username)
	if err == nil && existingUser != nil {
		return nil, errors.New("username already taken")
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		return nil, err
	}

	user := &model.User{
		Username: username,
		Email:    email,
		Password: string(hashedPassword),
		Avatar:   "",
	}

	if err := s.repo.Create(user); err != nil {
		return nil, err
	}

	return user, nil
}

func (s *UserService) Login(email, password string) (*model.User, error) {
	user, err := s.repo.GetByEmail(email)
	if err != nil {
		if err == sql.ErrNoRows {
			return nil, errors.New("invalid credentials")
		}
		return nil, err
	}

	if err := bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(password)); err != nil {
		return nil, errors.New("invalid credentials")
	}

	return user, nil
}

func (s *UserService) GetByID(id int64) (*model.User, error) {
	return s.repo.GetByID(id)
}

func (s *UserService) GetByEmail(email string) (*model.User, error) {
	return s.repo.GetByEmail(email)
}

func (s *UserService) List(limit, offset int) ([]*model.User, error) {
	if limit <= 0 {
		limit = 20
	}
	if offset < 0 {
		offset = 0
	}
	return s.repo.List(limit, offset)
}

func (s *UserService) Update(user *model.User) error {
	return s.repo.Update(user)
}

func (s *UserService) Delete(id int64) error {
	return s.repo.Delete(id)
}

func (s *UserService) UpdateAvatar(userID int64, avatarURL string) error {
	user, err := s.repo.GetByID(userID)
	if err != nil {
		return err
	}
	user.Avatar = avatarURL
	user.UpdatedAt = time.Now()
	return s.repo.Update(user)
}
