FROM openjdk:17-jdk-slim-buster
ENV PORT 8080
EXPOSE 8080
COPY Task/build/install/Task /app/
COPY .env /app/.env
RUN mkdir -p /app/logs
WORKDIR /app
ENTRYPOINT ["bin/Task"]