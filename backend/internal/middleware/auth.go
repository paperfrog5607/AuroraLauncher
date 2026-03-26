package middleware

import (
	"strings"

	"github.com/gofiber/fiber/v2"
	"github.com/golang-jwt/jwt/v5"
)

func Auth(secret string) fiber.Handler {
	return func(c *fiber.Ctx) error {
		authHeader := c.Get("Authorization")
		if authHeader == "" {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"code":    fiber.StatusUnauthorized,
				"message": "Missing authorization header",
			})
		}

		tokenString := strings.TrimPrefix(authHeader, "Bearer ")
		if tokenString == authHeader {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"code":    fiber.StatusUnauthorized,
				"message": "Invalid authorization format",
			})
		}

		token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
			if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
				return nil, jwt.ErrSignatureInvalid
			}
			return []byte(secret), nil
		})

		if err != nil || !token.Valid {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"code":    fiber.StatusUnauthorized,
				"message": "Invalid or expired token",
			})
		}

		claims, ok := token.Claims.(jwt.MapClaims)
		if !ok {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"code":    fiber.StatusUnauthorized,
				"message": "Invalid token claims",
			})
		}

		userID, ok := claims["user_id"].(float64)
		if !ok {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"code":    fiber.StatusUnauthorized,
				"message": "Invalid user ID in token",
			})
		}

		c.Locals("user_id", int64(userID))
		return c.Next()
	}
}
