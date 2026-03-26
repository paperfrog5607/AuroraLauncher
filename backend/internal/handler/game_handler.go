package handler

import (
	"strconv"

	"github.com/gofiber/fiber/v2"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/model"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/service"
	"github.com/paperfrog5607/AuroraLauncher/backend/pkg/response"
)

type GameHandler struct {
	gameService *service.GameService
}

func NewGameHandler(gameService *service.GameService) *GameHandler {
	return &GameHandler{gameService: gameService}
}

type CreateGameRequest struct {
	Name        string `json:"name"`
	Type        string `json:"type"`
	Platform    string `json:"platform"`
	InstallPath string `json:"install_path"`
	Version     string `json:"version"`
	Icon        string `json:"icon"`
	Cover       string `json:"cover"`
	Description string `json:"description"`
}

func (h *GameHandler) List(c *fiber.Ctx) error {
	limit, _ := strconv.Atoi(c.Query("limit", "20"))
	offset, _ := strconv.Atoi(c.Query("offset", "0"))

	games, err := h.gameService.List(limit, offset)
	if err != nil {
		return response.InternalError(c, "Failed to list games")
	}

	if games == nil {
		games = []*model.Game{}
	}

	return c.JSON(response.Success(fiber.Map{
		"games":  games,
		"limit":  limit,
		"offset": offset,
	}))
}

func (h *GameHandler) Get(c *fiber.Ctx) error {
	id, err := strconv.ParseInt(c.Params("id"), 10, 64)
	if err != nil {
		return response.BadRequest(c, "Invalid game ID")
	}

	game, err := h.gameService.GetByID(id)
	if err != nil {
		return response.NotFound(c, "Game not found")
	}

	return c.JSON(response.Success(game))
}

func (h *GameHandler) Create(c *fiber.Ctx) error {
	userID := c.Locals("user_id").(int64)

	var req CreateGameRequest
	if err := c.BodyParser(&req); err != nil {
		return response.BadRequest(c, "Invalid request body")
	}

	if req.Name == "" {
		return response.BadRequest(c, "Game name is required")
	}

	game := &model.Game{
		Name:        req.Name,
		Type:        req.Type,
		Platform:    req.Platform,
		InstallPath: req.InstallPath,
		Version:     req.Version,
		Icon:        req.Icon,
		Cover:       req.Cover,
		Description: req.Description,
		UserID:      userID,
	}

	if err := h.gameService.Create(game); err != nil {
		return response.InternalError(c, "Failed to create game")
	}

	return c.Status(fiber.StatusCreated).JSON(response.Success(game))
}

func (h *GameHandler) Update(c *fiber.Ctx) error {
	id, err := strconv.ParseInt(c.Params("id"), 10, 64)
	if err != nil {
		return response.BadRequest(c, "Invalid game ID")
	}

	game, err := h.gameService.GetByID(id)
	if err != nil {
		return response.NotFound(c, "Game not found")
	}

	var req CreateGameRequest
	if err := c.BodyParser(&req); err != nil {
		return response.BadRequest(c, "Invalid request body")
	}

	game.Name = req.Name
	game.Type = req.Type
	game.Platform = req.Platform
	game.InstallPath = req.InstallPath
	game.Version = req.Version
	game.Icon = req.Icon
	game.Cover = req.Cover
	game.Description = req.Description

	if err := h.gameService.Update(game); err != nil {
		return response.InternalError(c, "Failed to update game")
	}

	return c.JSON(response.Success(game))
}

func (h *GameHandler) Delete(c *fiber.Ctx) error {
	id, err := strconv.ParseInt(c.Params("id"), 10, 64)
	if err != nil {
		return response.BadRequest(c, "Invalid game ID")
	}

	if err := h.gameService.Delete(id); err != nil {
		return response.InternalError(c, "Failed to delete game")
	}

	return c.JSON(response.Success(fiber.Map{"message": "Game deleted successfully"}))
}
