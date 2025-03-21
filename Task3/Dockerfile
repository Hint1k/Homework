FROM openjdk:17-jdk-slim-buster
ENV PORT 8080
EXPOSE 8080
COPY Task3/build/libs/Task3-0.0.1-SNAPSHOT-all.jar /app.jar
COPY .env /app/.env
COPY /Task3/src/main/resources/logging.properties /app/logging.properties
WORKDIR /app
ENTRYPOINT ["java", "-Djava.util.logging.config.file=/app/logging.properties", "-jar", "/app.jar"]