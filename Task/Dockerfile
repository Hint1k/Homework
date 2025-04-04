FROM openjdk:17-jdk-slim-buster
ENV PORT 8080
EXPOSE 8080
COPY Task/build/install/Task /app/
COPY .env /app/.env
COPY Task/src/main/resources/application.yml /app/application.yml
#COPY /Task/src/main/resources/logging.properties /app/logging.properties
RUN mkdir -p /app/logs
WORKDIR /app
ENTRYPOINT ["bin/Task"]