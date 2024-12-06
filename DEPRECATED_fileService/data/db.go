package data

import (
	"bytes"
	"context"
	"fmt"
	"log"
	"os"

	"time"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/gridfs"
	"go.mongodb.org/mongo-driver/mongo/options"
)

type gridfsFile struct {
	Id     string `bson:"_id"`
	Name   string `bson:"filename"`
	Length int64  `bson:"length"`
}

func InitMongoClient() *mongo.Client {
	var err error
	var client *mongo.Client
	uri := os.Getenv("MONGODB_URL") //"mongodb://localhost:27017"
	opts := options.Client()
	opts.ApplyURI(uri)
	opts.SetMaxPoolSize(5)
	if client, err = mongo.Connect(context.Background(), opts); err != nil {
		fmt.Println(err.Error())
	}
	return client
}

func GetAllFilesRecords() []gridfsFile {
	conn := InitMongoClient()

	bucket, err := gridfs.NewBucket(
		conn.Database("myfiles"),
	)
	filter := bson.D{}
	cursor, err := bucket.Find(filter)
	if err != nil {
		panic(err)
	}

	var foundFiles []gridfsFile
	if err = cursor.All(context.TODO(), &foundFiles); err != nil {
		panic(err)
	}

	return foundFiles
}

//Функция для загрузки файлов в MongoDB GridFS
//Принимает байт-массив данных из файла и имя файла
func UploadFile(data []byte, filename string) primitive.ObjectID {
	conn := InitMongoClient()       //Инициализация подключения к mongodb
	bucket, err := gridfs.NewBucket(conn.Database("myfiles"),)
	if err != nil {log.Fatal(err)}  //Проверка на наличие ошибок
	//Инициализация нового файла в хранилище
	uploadStream, err := bucket.OpenUploadStream(
		filename,
	)
	if err != nil {fmt.Println(err)} //Проверка на наличие ошибок
	defer uploadStream.Close()
    //Запись байт-массива данных из файла в хранилище
	fileSize, err := uploadStream.Write(data)
	if err != nil {log.Fatal(err)} //Проверка на наличие ошибок
	fileId := uploadStream.FileID   //Запись ID файла
	log.Printf("Write file to DB was successful. File size: %d M\n", fileSize)
	//Возврат ID файла
	return fileId.(primitive.ObjectID)
}

func getFileNameById(id string) string {
	conn := InitMongoClient()
	db := conn.Database("myfiles")
	fsFiles := db.Collection("fs.files")
	ctx, _ := context.WithTimeout(context.Background(), 10*time.Second)
	objectId, err := primitive.ObjectIDFromHex(id)
	if err != nil {
		fmt.Println(err)
	}
	var result gridfsFile
	if err := fsFiles.FindOne(ctx, bson.M{"_id": objectId}).Decode(&result); err != nil {
		fmt.Println(err)
	}

	fmt.Println(result)
	return result.Name
}

func DownloadFile(id string) []byte {
	conn := InitMongoClient()
	// For CRUD operations, here is an example
	db := conn.Database("myfiles")
	fsFiles := db.Collection("fs.files")

	fileName := getFileNameById(id)
	ctx, _ := context.WithTimeout(context.Background(), 10*time.Second)
	var results bson.M
	err := fsFiles.FindOne(ctx, bson.M{}).Decode(&results)
	if err != nil {
		log.Fatal(err)
	}
	// you can print out the results
	fmt.Println(results)

	bucket, _ := gridfs.NewBucket(
		db,
	)
	var buf bytes.Buffer
	dStream, err := bucket.DownloadToStreamByName(fileName, &buf)
	if err != nil {
		//log.Panic(err)
		return []byte("not found")
	}
	fmt.Printf("File size to download: %v\n", dStream)
	//ioutil.WriteFile(fileName, buf.Bytes(), 0600)
	return buf.Bytes()
}
//Функция для удаления файла из хранилища
func DeleteFile(id string) {
    //Подлкючение к хранилищу
	conn := InitMongoClient()
	//Поиск коллекции
	collection := conn.Database("myfiles").Collection("fs.files")
	//Таймаут операции
	ctx, _ := context.WithTimeout(context.Background(), 10*time.Second)
	//Преобразование string ObjectID в ObjectID для драйвера MongoDB
	objectId, err := primitive.ObjectIDFromHex(id)
	if err != nil {
		fmt.Println(err)
		return
	}
	//Удаление записи
	collection.FindOneAndDelete(ctx, bson.M{"_id": objectId})
}
