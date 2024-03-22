FROM openjdk:21-jdk

COPY build/libs/chatting-api-0.0.1-SNAPSHOT.jar chatting-api/chatting-app.jar

WORKDIR /chatting-api
ENTRYPOINT ["java", "-jar", "chatting-app.jar"]