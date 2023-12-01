FROM maven:latest as build
COPY . .
RUN ["mvn", "package"]

FROM openjdk:19
COPY --from=build 'target/DigitalBookmark-0.0.1-SNAPSHOT.jar' application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]

