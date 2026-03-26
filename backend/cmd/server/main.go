package main

import (
	"context"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/cors"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/gofiber/fiber/v2/middleware/recover"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/config"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/handler"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/middleware"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/repository"
	"github.com/paperfrog5607/AuroraLauncher/backend/internal/service"
	"github.com/paperfrog5607/AuroraLauncher/backend/pkg/response"
)

func main() {
	cfg := config.Load()

	db, err := repository.NewDatabase(cfg.DatabaseURL)
	if err != nil {
		log.Fatalf("Failed to connect to database: %v", err)
	}
	defer db.Close()

	if err := repository.RunMigrations(db); err != nil {
		log.Fatalf("Failed to run migrations: %v", err)
	}

	userRepo := repository.NewUserRepository(db)
	gameRepo := repository.NewGameRepository(db)
	modRepo := repository.NewModRepository(db)
	modpackRepo := repository.NewModpackRepository(db)

	userService := service.NewUserService(userRepo)
	gameService := service.NewGameService(gameRepo)
	modService := service.NewModService(modRepo)
	modpackService := service.NewModpackService(modpackRepo)

	authHandler := handler.NewAuthHandler(userService, cfg.JWTSecret)
	gameHandler := handler.NewGameHandler(gameService)
	modHandler := handler.NewModHandler(modService)
	modpackHandler := handler.NewModpackHandler(modpackService)

	app := fiber.New(fiber.Config{
		AppName:      "Aurora Server",
		ReadTimeout:  10 * time.Second,
		WriteTimeout: 10 * time.Second,
		ErrorHandler: response.ErrorHandler,
	})

	app.Use(recover.New())
	app.Use(logger.New())
	app.Use(cors.New(cors.Config{
		AllowOrigins: "*",
		AllowMethods: "GET,POST,PUT,DELETE,OPTIONS",
		AllowHeaders: "Origin,Content-Type,Accept,Authorization",
	}))

	app.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(response.Success(fiber.Map{"status": "ok"}))
	})

	api := app.Group("/api/v1")

	api.Post("/auth/register", authHandler.Register)
	api.Post("/auth/login", authHandler.Login)

	games := api.Group("/games")
	games.Get("/", gameHandler.List)
	games.Get("/:id", gameHandler.Get)
	games.Post("/", middleware.Auth(cfg.JWTSecret), gameHandler.Create)
	games.Put("/:id", middleware.Auth(cfg.JWTSecret), gameHandler.Update)
	games.Delete("/:id", middleware.Auth(cfg.JWTSecret), gameHandler.Delete)

	mods := api.Group("/mods")
	mods.Get("/", modHandler.List)
	mods.Get("/:id", modHandler.Get)
	mods.Post("/", middleware.Auth(cfg.JWTSecret), modHandler.Create)
	mods.Put("/:id", middleware.Auth(cfg.JWTSecret), modHandler.Update)
	mods.Delete("/:id", middleware.Auth(cfg.JWTSecret), modHandler.Delete)

	modpacks := api.Group("/modpacks")
	modpacks.Get("/", modpackHandler.List)
	modpacks.Get("/:id", modpackHandler.Get)
	modpacks.Post("/", middleware.Auth(cfg.JWTSecret), modpackHandler.Create)
	modpacks.Put("/:id", middleware.Auth(cfg.JWTSecret), modpackHandler.Update)
	modpacks.Delete("/:id", middleware.Auth(cfg.JWTSecret), modpackHandler.Delete)

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)

	go func() {
		if err := app.Listen(":" + cfg.Port); err != nil {
			log.Fatalf("Failed to start server: %v", err)
		}
	}()

	log.Printf("Server started on port %s", cfg.Port)
	<-quit

	log.Println("Shutting down server...")
	if err := app.ShutdownWithTimeout(30 * time.Second); err != nil {
		log.Fatalf("Server forced to shutdown: %v", err)
	}

	log.Println("Server exited properly")
	_ = context.Background()
}
