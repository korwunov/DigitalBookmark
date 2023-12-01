package main

import (
	"fmt"
	"log"
	"net/http"
	"os"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
	_ "github.com/joho/godotenv/autoload"
	"pr9.com/m/app/middleware"
	"pr9.com/m/data"
)

func getFiles(c *gin.Context) {
	list := data.GetAllFilesRecords()
	c.IndentedJSON(http.StatusOK, list)
}

func getFileById(c *gin.Context) {
	fileId := c.Params.ByName("id")
	userId := c.Params.ByName("userId")
	middleware.GetFileById(fileId, userId, c.Writer, c.Request)
}

func addNewFile(c *gin.Context) {
	fileName := c.Params.ByName("name")
	userId := c.Params.ByName("userId")
	middleware.AddFile(fileName, c.Writer, c.Request, userId)
}

func deleteFile(c *gin.Context) {
	fileId := c.Params.ByName("id")
	userId := c.Params.ByName("userId")
	middleware.DeleteFile(fileId, c.Writer, c.Request, userId)
}

func test(c *gin.Context) {
	c.IndentedJSON(http.StatusAccepted, "here are go server")
}

func main() {
	godotenv.Load()
	fmt.Println(os.Getenv("MONGODB_URL"))
	r := gin.Default()
	api := r.Group("/files")
	{
		api.GET("", getFiles)
		api.GET("/:id/:userId", getFileById)
		api.POST("/:name/:userId", addNewFile)
		api.DELETE("/:id/:userId", deleteFile)
		api.GET("/test", test)
	}
	log.Fatal(r.Run(":" + os.Getenv("APP_PORT")))
}
