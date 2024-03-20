FROM openjdk:21-jdk

COPY build/libs/admin-api-0.0.1-SNAPSHOT.jar admin-api/admin-app.jar

WORKDIR /admin-api
ENTRYPOINT ["java", "-jar", "admin-app.jar"]