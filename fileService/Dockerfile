#Загрузка базового образа golang
FROM golang:alpine AS builder
#Установка рабочей директории
WORKDIR /app
#Копирование исходного кода в контейнер
COPY . .
#Загрузка зависимостей
RUN go mod download
#Заупск сборки с флагами имени и архитекруты ОС
RUN GOOS=linux GOARCH=amd64 go build -o main app/main.go

#Загрузка alpine образа
FROM alpine:latest
#Копирование
COPY --from=builder app/main /main
#Открытие портов контейнера
EXPOSE 4200:4200
#Запуск сервиса
ENTRYPOINT ["/main"]