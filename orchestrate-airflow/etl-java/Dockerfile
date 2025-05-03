FROM openjdk:23-ea-slim

WORKDIR /app

COPY target/*-jar-with-dependencies.jar /app.jar

COPY .dockerenv .env
CMD ["java", "-jar", "/app.jar"]