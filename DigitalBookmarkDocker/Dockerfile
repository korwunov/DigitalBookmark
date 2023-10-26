FROM openjdk:19
ARG JAR_FILE=DigitalBookmark-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]