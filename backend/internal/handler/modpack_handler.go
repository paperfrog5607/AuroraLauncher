package handler

import (
	"strconv"

	"github.com/gofiber/fiber/v2"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/model"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/service"
	"github.com/paperfrog5607/AuroraLauncher/backend/pkg/response"
)

type ModpackHandler struct {
	modpackService *service.ModpackService
}

func NewModpackHandler(modpackService *service.ModpackService) *ModpackHandler {
	return &ModpackHandler{modpackService: modpackService}
}

type CreateModpackRequest struct {
	Name             string `json:"name"`
	Version          string `json:"version"`
	Author           string `json:"author"`
	Description      string `json:"description"`
	DownloadURL      string `json:"download_url"`
	ImageURL         string `json:"image_url"`
	MinecraftVersion string `json:"minecraft_version"`
	GameID           int64  `json:"game_id"`
}

func (h *ModpackHandler) List(c *fiber.Ctx) error {
	limit, _ := strconv.Atoi(c.Query("limit", "20"))
	offset, _ := strconv.Atoi(c.Query("offset", "0"))

	modpacks, err := h.modpackService.List(limit, offset)
	if err != nil {
		return response.InternalError(c, "Failed to list modpacks")
	}

	if modpacks == nil {
		modpacks = []*model.Modpack{}
	}

	return c.JSON(response.Success(fiber.Map{
		"modpacks": modpacks,
		"limit":    limit,
		"offset":   offset,
	}))
}

func (h *ModpackHandler) Get(c *fiber.Ctx) error {
	id, err := strconv.ParseInt(c.Params("id"), 10, 64)
	if err != nil {
		return response.BadRequest(c, "Invalid modpack ID")
	}

	modpack, err := h.modpackService.GetByID(id)
	if err != nil {
		return response.NotFound(c, "Modpack not found")
	}

	return c.JSON(response.Success(modpack))
}

func (h *ModpackHandler) Create(c *fiber.Ctx) error {
	userID := c.Locals("user_id").(int64)

	var req CreateModpackRequest
	if err := c.BodyParser(&req); err != nil {
		return response.BadRequest(c, "Invalid request body")
	}

	if req.Name == "" {
		return response.BadRequest(c, "Modpack name is required")
	}

	modpack := &model.Modpack{
		Name:             req.Name,
		Version:          req.Version,
		Author:           req.Author,
		Description:      req.Description,
		DownloadURL:      req.DownloadURL,
		ImageURL:         req.ImageURL,
		MinecraftVersion: req.MinecraftVersion,
		GameID:           req.GameID,
		UserID:           userID,
	}

	if err := h.modpackService.Create(modpack); err != nil {
		return response.InternalError(c, "Failed to create modpack")
	}

	return c.Status(fiber.StatusCreated).JSON(response.Success(modpack))
}

func (h *ModpackHandler) Update(c *fiber.Ctx) error {
	id, err := strconv.ParseInt(c.Params("id"), 10, 64)
	if err != nil {
		return response.BadRequest(c, "Invalid modpack ID")
	}

	modpack, err := h.modpackService.GetByID(id)
	if err != nil {
		return response.NotFound(c, "Modpack not found")
	}

	var req CreateModpackRequest
	if err := c.BodyParser(&req); err != nil {
		return response.BadRequest(c, "Invalid request body")
	}

	modpack.Name = req.Name
	modpack.Version = req.Version
	modpack.Author = req.Author
	modpack.Description = req.Description
	modpack.DownloadURL = req.DownloadURL
	modpack.ImageURL = req.ImageURL
	modpack.MinecraftVersion = req.MinecraftVersion

	if err := h.modpackService.Update(modpack); err != nil {
		return response.InternalError(c, "Failed to update modpack")
	}

	return c.JSON(response.Success(modpack))
}

func (h *ModpackHandler) Delete(c *fiber.Ctx) error {
	id, err := strconv.ParseInt(c.Params("id"), 10, 64)
	if err != nil {
		return response.BadRequest(c, "Invalid modpack ID")
	}

	if err := h.modpackService.Delete(id); err != nil {
		return response.InternalError(c, "Failed to delete modpack")
	}

	return c.JSON(response.Success(fiber.Map{"message": "Modpack deleted successfully"}))
}
