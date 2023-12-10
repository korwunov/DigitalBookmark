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
    //Получение параметров по имени
	fileId := c.Params.ByName("id")
	userId := c.Params.ByName("userId")
	//Вызов метода из сервиса
	middleware.DeleteFile(fileId, c.Writer, c.Request, userId)
}

func test(c *gin.Context) {
	c.IndentedJSON(http.StatusAccepted, "here are go server")
}
//Главная функиця - точка входа в приложение
func main() {
	godotenv.Load()     //Загрузка переменных окружения
	fmt.Println(os.Getenv("MONGODB_URL"))
	r := gin.Default()  //Инициализация роутера
	api := r.Group("/files")    //Группировка роутера для запросов с /files
	{
		api.GET("", getFiles)   //GET запрос на получение списка всех файлов
		api.GET("/:id/:userId", getFileById)    //Скачивание файла по ID
		api.POST("/:name/:userId", addNewFile)  //Загрузка файла
		api.DELETE("/:id/:userId", deleteFile)  //Удаление файла
		api.GET("/test", test)
	}
	//Запуск роутера, логирование в случае ошибки
	log.Fatal(r.Run(":" + os.Getenv("APP_PORT")))
}
