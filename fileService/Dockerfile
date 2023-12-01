FROM golang:alpine AS builder

WORKDIR /app

COPY . .

RUN go mod download

RUN GOOS=linux GOARCH=amd64 go build -o main app/main.go

FROM alpine:latest

COPY --from=builder app/main /main

EXPOSE 4200:4200

ENTRYPOINT ["/main"]