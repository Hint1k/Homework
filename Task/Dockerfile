FROM openjdk:17-jdk-slim-buster
ENV PORT 8080
EXPOSE 8080
COPY Task/build/libs/Task-0.0.1-SNAPSHOT-all.jar /app.jar
COPY .env /app/.env
COPY Task/src/main/resources/application.yml /app/application.yml
COPY /Task/src/main/resources/logging.properties /app/logging.properties
RUN mkdir -p /app/logs
WORKDIR /app
ENTRYPOINT ["java", "-Djava.util.logging.config.file=/app/logging.properties", "-jar", "/app.jar"]