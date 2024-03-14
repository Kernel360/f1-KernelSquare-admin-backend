FROM openjdk:21.0.1-jre-slim
WORKDIR /app
COPY build/libs/admin-api-0.0.1-SNAPSHOT.jar kernelsquare-admin-app.jar
ENTRYPOINT ["java", "-jar", "kernelsquare-admin-app.jar"]