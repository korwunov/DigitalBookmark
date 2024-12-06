FROM maven:latest as build
COPY . .
RUN ["mvn", "package", "-Dmaven.test.skip"]

FROM amazoncorretto:21
COPY --from=build 'target/application-0.0.1-SNAPSHOT.jar' application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
