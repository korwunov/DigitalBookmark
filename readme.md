билд образов для докера: `mvn clean package spring-boot:build-image`
настроить сокет докер демона в корневом pom.xml

для билда образа для клиента необходимо получить .jar `mvn clean package -Pproduction` (в директории Client)
затем собрать docker образ из Dockerfile в директории Client
полученный образ использовать в корневом docker-compose.yml

Ниже для k8s:
после билда образов необходимо загрузить их в minikube командой
`minikube image load <имя образа>:<тэг образа>`

minikube image rm auth-service:0.0.1-SNAPSHOT && minikube image rm file-service:0.0.1-SNAPSHOT && minikube image rm bookmark-service:0.0.1-SNAPSHOT
minikube image load bookmark-service:0.0.1-SNAPSHOT && minikube image load file-service:0.0.1-SNAPSHOT && minikube image load auth-service:0.0.1-SNAPSHOT

далее необходимо зайти в папку k8s, выполнить следующие команды
