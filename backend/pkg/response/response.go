package response

import (
	"github.com/gofiber/fiber/v2"
)

type Response struct {
	Code    int         `json:"code"`
	Message string      `json:"message"`
	Data    interface{} `json:"data,omitempty"`
}

func Success(data interface{}) Response {
	return Response{
		Code:    0,
		Message: "success",
		Data:    data,
	}
}

func Error(code int, message string) Response {
	return Response{
		Code:    code,
		Message: message,
	}
}

func ErrorHandler(c *fiber.Ctx, err error) error {
	code := fiber.StatusInternalServerError
	message := "Internal Server Error"

	if e, ok := err.(*fiber.Error); ok {
		code = e.Code
		message = e.Message
	}

	return c.Status(code).JSON(Error(code, message))
}

func BadRequest(c *fiber.Ctx, message string) error {
	return c.Status(fiber.StatusBadRequest).JSON(Error(fiber.StatusBadRequest, message))
}

func Unauthorized(c *fiber.Ctx, message string) error {
	return c.Status(fiber.StatusUnauthorized).JSON(Error(fiber.StatusUnauthorized, message))
}

func NotFound(c *fiber.Ctx, message string) error {
	return c.Status(fiber.StatusNotFound).JSON(Error(fiber.StatusNotFound, message))
}

func InternalError(c *fiber.Ctx, message string) error {
	return c.Status(fiber.StatusInternalServerError).JSON(Error(fiber.StatusInternalServerError, message))
}
