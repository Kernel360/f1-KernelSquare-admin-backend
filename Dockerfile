FROM openjdk:21-jdk-slim
WORKDIR /app
COPY admin-api/build/libs/admin-api-0.0.1-SNAPSHOT.jar kernelsquare-admin-app.jar
ENTRYPOINT ["java", "-jar", "kernelsquare-admin-app.jar"]