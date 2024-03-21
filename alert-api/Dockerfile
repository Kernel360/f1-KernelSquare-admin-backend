FROM openjdk:21-jdk

COPY build/libs/alert-api-0.0.1-SNAPSHOT.jar alert-api/alert-app.jar
COPY build/resources/main/static/docs /app/static/docs

WORKDIR /alert-api
ENTRYPOINT ["java", "-jar", "alert-app.jar"]
