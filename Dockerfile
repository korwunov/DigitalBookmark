#Загрузка базового образа maven
FROM maven:latest as build
#Копирование исходного кода из директории проекта
COPY . .
#Запуск сборки JAR файла
RUN ["mvn", "package"]

#Загрузка базового образа openjdk
FROM openjdk:19
#Копирование JAR файла
COPY --from=build 'target/DigitalBookmark-0.0.1-SNAPSHOT.jar' application.jar
#Запуск приложения
ENTRYPOINT ["java", "-jar", "application.jar"]

