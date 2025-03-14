FROM openjdk:17-jdk-slim-buster
ENV PORT 8080
EXPOSE 8080
COPY Task2/build/libs/Task2-0.0.1-SNAPSHOT-all.jar /app.jar
COPY .env /app/.env
WORKDIR /app
ENTRYPOINT ["java","-jar","/app.jar"]