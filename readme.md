билд образов для докера: mvn clean package spring-boot:build-image \
настроить сокет докер демона в корневом pom.xml \
\
после билда образов необходимо загрузить их в minikube командой \
minikube image load <имя образа>:<тэг образа> \
\
далее необходимо зайти в папку k8s, выполнить следующие команды \
