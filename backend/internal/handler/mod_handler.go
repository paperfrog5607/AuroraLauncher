package handler

import (
	"strconv"

	"github.com/gofiber/fiber/v2"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/model"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/service"
	"github.com/paperfrog5607/AuroraLauncher/backend/pkg/response"
)

type ModHandler struct {
	modService *service.ModService
}

func NewModHandler(modService *service.ModService) *ModHandler {
	return &ModHandler{modService: modService}
}

type CreateModRequest struct {
	Name        string `json:"name"`
	Version     string `json:"version"`
	Author      string `json:"author"`
	Description string `json:"description"`
	DownloadURL string `json:"download_url"`
	ImageURL    string `json:"image_url"`
	GameID      int64  `json:"game_id"`
}

func (h *ModHandler) List(c *fiber.Ctx) error {
	limit, _ := strconv.Atoi(c.Query("limit", "20"))
	offset, _ := strconv.Atoi(c.Query("offset", "0"))

	mods, err := h.modService.List(limit, offset)
	if err != nil {
		return response.InternalError(c, "Failed to list mods")
	}

	if mods == nil {
		mods = []*model.Mod{}
	}

	return c.JSON(response.Success(fiber.Map{
		"mods":   mods,
		"limit":  limit,
		"offset": offset,
	}))
}

func (h *ModHandler) Get(c *fiber.Ctx) error {
	id, err := strconv.ParseInt(c.Params("id"), 10, 64)
	if err != nil {
		return response.BadRequest(c, "Invalid mod ID")
	}

	mod, err := h.modService.GetByID(id)
	if err != nil {
		return response.NotFound(c, "Mod not found")
	}

	return c.JSON(response.Success(mod))
}

func (h *ModHandler) Create(c *fiber.Ctx) error {
	userID := c.Locals("user_id").(int64)

	var req CreateModRequest
	if err := c.BodyParser(&req); err != nil {
		return response.BadRequest(c, "Invalid request body")
	}

	if req.Name == "" {
		return response.BadRequest(c, "Mod name is required")
	}

	mod := &model.Mod{
		Name:        req.Name,
		Version:     req.Version,
		Author:      req.Author,
		Description: req.Description,
		DownloadURL: req.DownloadURL,
		ImageURL:    req.ImageURL,
		GameID:      req.GameID,
		UserID:      userID,
	}

	if err := h.modService.Create(mod); err != nil {
		return response.InternalError(c, "Failed to create mod")
	}

	return c.Status(fiber.StatusCreated).JSON(response.Success(mod))
}

func (h *ModHandler) Update(c *fiber.Ctx) error {
	id, err := strconv.ParseInt(c.Params("id"), 10, 64)
	if err != nil {
		return response.BadRequest(c, "Invalid mod ID")
	}

	mod, err := h.modService.GetByID(id)
	if err != nil {
		return response.NotFound(c, "Mod not found")
	}

	var req CreateModRequest
	if err := c.BodyParser(&req); err != nil {
		return response.BadRequest(c, "Invalid request body")
	}

	mod.Name = req.Name
	mod.Version = req.Version
	mod.Author = req.Author
	mod.Description = req.Description
	mod.DownloadURL = req.DownloadURL
	mod.ImageURL = req.ImageURL

	if err := h.modService.Update(mod); err != nil {
		return response.InternalError(c, "Failed to update mod")
	}

	return c.JSON(response.Success(mod))
}

func (h *ModHandler) Delete(c *fiber.Ctx) error {
	id, err := strconv.ParseInt(c.Params("id"), 10, 64)
	if err != nil {
		return response.BadRequest(c, "Invalid mod ID")
	}

	if err := h.modService.Delete(id); err != nil {
		return response.InternalError(c, "Failed to delete mod")
	}

	return c.JSON(response.Success(fiber.Map{"message": "Mod deleted successfully"}))
}
