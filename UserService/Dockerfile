FROM eclipse-temurin:17-jdk
EXPOSE 8770
ADD target/user-service-1.0.0.jar /app/user-service.jar
ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]
