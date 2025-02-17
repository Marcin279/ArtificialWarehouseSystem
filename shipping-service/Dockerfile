FROM eclipse-temurin:22-jdk
WORKDIR /app
COPY target/shipping-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
