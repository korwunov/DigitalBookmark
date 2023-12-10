package middleware

import (
	"bytes"
	"context"
	"encoding/base64"
	"encoding/gob"
	"encoding/json"
	"fmt"
	"log"
	"os"
	"strconv"

	"time"

	amqp "github.com/rabbitmq/amqp091-go"
)

type Message struct {
	Action string `json:"action"`
	UserId int    `json:"userId"`
	FileId string `json:"fileId"`
}

func ToGOB64(m Message) string {
	gob.Register(Message{})
	b := bytes.Buffer{}
	e := gob.NewEncoder(&b)
	err := e.Encode(m)
	if err != nil {
		fmt.Println(`failed gob Encode`, err)
	}
	return base64.StdEncoding.EncodeToString(b.Bytes())
}

func serialize(msg Message) ([]byte, error) {
	var b bytes.Buffer
	encoder := json.NewEncoder(&b)
	err := encoder.Encode(msg)
	return b.Bytes(), err
}

func deserialize(b []byte) (Message, error) {
	var msg Message
	buf := bytes.NewBuffer(b)
	decoder := json.NewDecoder(buf)
	err := decoder.Decode(&msg)
	return msg, err
}

//Метод отпарвки сообщения о загрузке/удалении файла
func sendFileInfo(actionValue string, userIdValue string, fileIdValue string) {
    //Получение данных для подключения к RabbitMQ
	rabbitHost := os.Getenv("RABBITMQ_HOST")     //"localhost"
	rabbitPort := os.Getenv("RABBITMQ_PORT")     //"5672"
	rabbitUser := os.Getenv("RABBITMQ_USERNAME") //"guest"
	rabbitPass := os.Getenv("RABBITMQ_PASSWORD") //"guest"
    //Открытие соединения c брокером
	conn, err := amqp.Dial("amqp://" +
		rabbitUser + ":" +
		rabbitPass + "@" +
		rabbitHost + ":" +
		rabbitPort + "/") // Создаем подключение к RabbitMQ

	if err != nil {
		log.Fatalf("unable to open connect to RabbitMQ server. Error: %s", err)
	}
    //Открытие канала
	channel, err := conn.Channel()
	if err != nil {
		log.Fatalf("failed to open channel. Error: %s", err)
	}
    //Инициализация очереди
	q, err := channel.QueueDeclare(
		"digitalbookmark_file_queue",
		true,
		false,
		false,
		false,
		nil,
	)
    //Установка контекста с таймаутом отпарвки
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	if err != nil {
		log.Fatalf("failed to declare a queue. Error: %s", err)
	}

	fmt.Println(actionValue)
	fmt.Println(userIdValue)
	fmt.Println(fileIdValue)
    //Метод отправки сообщения в очередь
	err = channel.PublishWithContext(ctx,
		"",
		q.Name,
		false,
		false,
		amqp.Publishing{    //Конвертация сообщения
			ContentType: "application/json",
			Body:        []byte(actionValue + ";" + userIdValue + ";" + fileIdValue)})

	defer func() {
		_ = channel.Close()     //Закрытие канала
		_ = conn.Close()        //Закрытие подключения в случае удачной попытки
	}()
}

func isFileBelongsToUser(fileId string, userId string) (result bool) {
	rabbitHost := os.Getenv("RABBITMQ_HOST")     //"localhost"
	rabbitPort := os.Getenv("RABBITMQ_PORT")     //"5672"
	rabbitUser := os.Getenv("RABBITMQ_USERNAME") //"guest"
	rabbitPass := os.Getenv("RABBITMQ_PASSWORD") //"guest"

	conn, err := amqp.Dial("amqp://" +
		rabbitUser + ":" +
		rabbitPass + "@" +
		rabbitHost + ":" +
		rabbitPort + "/") // Создаем подключение к RabbitMQ

	if err != nil {
		log.Fatalf("unable to open connect to RabbitMQ server. Error: %s", err)
	}

	channel, err := conn.Channel()
	if err != nil {
		log.Fatalf("failed to open channel. Error: %s", err)
	}

	q, err := channel.QueueDeclare(
		"digitalbookmark_file_permission_queue",
		true,
		false,
		false,
		false,
		nil,
	)

	msgs, err := channel.Consume(
		q.Name, // queue
		"",     // consumer
		true,   // auto-ack
		false,  // exclusive
		false,  // no-local
		false,  // no-wait
		nil,    // args
	)

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	if err != nil {
		log.Fatalf("failed to declare a queue. Error: %s", err)
	}

	err = channel.PublishWithContext(ctx,
		"",
		q.Name,
		false,
		false,
		amqp.Publishing{
			ContentType: "application/json",
			ReplyTo:     q.Name,
			Body:        []byte(fileId + ";" + userId),
		})
	defer func() {
		_ = channel.Close()
		_ = conn.Close() // Закрываем подключение в случае удачной попытки
	}()

	for d := range msgs {
		result, err := strconv.ParseBool(string(d.Body))
		if err != nil {
			fmt.Println(err)
		}
		return result
	}
	return
}
