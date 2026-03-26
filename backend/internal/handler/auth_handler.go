package handler

import (
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/golang-jwt/jwt/v5"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/model"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/service"
	"github.com/paperfrog5607/AuroraLauncher/backend/pkg/response"
)

type AuthHandler struct {
	userService *service.UserService
	jwtSecret   string
}

func NewAuthHandler(userService *service.UserService, jwtSecret string) *AuthHandler {
	return &AuthHandler{
		userService: userService,
		jwtSecret:   jwtSecret,
	}
}

type RegisterRequest struct {
	Username string `json:"username"`
	Email    string `json:"email"`
	Password string `json:"password"`
}

type LoginRequest struct {
	Email    string `json:"email"`
	Password string `json:"password"`
}

type AuthResponse struct {
	Token string      `json:"token"`
	User  *model.User `json:"user"`
}

func (h *AuthHandler) Register(c *fiber.Ctx) error {
	var req RegisterRequest
	if err := c.BodyParser(&req); err != nil {
		return response.BadRequest(c, "Invalid request body")
	}

	if req.Username == "" || req.Email == "" || req.Password == "" {
		return response.BadRequest(c, "Username, email and password are required")
	}

	user, err := h.userService.Register(req.Username, req.Email, req.Password)
	if err != nil {
		return response.BadRequest(c, err.Error())
	}

	token, err := h.generateToken(user.ID)
	if err != nil {
		return response.InternalError(c, "Failed to generate token")
	}

	return c.Status(fiber.StatusCreated).JSON(response.Success(AuthResponse{
		Token: token,
		User:  user,
	}))
}

func (h *AuthHandler) Login(c *fiber.Ctx) error {
	var req LoginRequest
	if err := c.BodyParser(&req); err != nil {
		return response.BadRequest(c, "Invalid request body")
	}

	if req.Email == "" || req.Password == "" {
		return response.BadRequest(c, "Email and password are required")
	}

	user, err := h.userService.Login(req.Email, req.Password)
	if err != nil {
		return response.Unauthorized(c, err.Error())
	}

	token, err := h.generateToken(user.ID)
	if err != nil {
		return response.InternalError(c, "Failed to generate token")
	}

	return c.JSON(response.Success(AuthResponse{
		Token: token,
		User:  user,
	}))
}

func (h *AuthHandler) generateToken(userID int64) (string, error) {
	claims := jwt.MapClaims{
		"user_id": userID,
		"exp":     time.Now().Add(24 * time.Hour * 7).Unix(),
		"iat":     time.Now().Unix(),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString([]byte(h.jwtSecret))
}
