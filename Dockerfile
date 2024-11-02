
FROM openjdk:23-slim
WORKDIR /app
COPY target/your-spring-boot-app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
