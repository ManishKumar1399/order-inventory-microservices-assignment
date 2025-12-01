# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jre-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the application's JAR file into the container at /app
# We'll assume the JAR is built and located in 'target/' relative to the Dockerfile
# Before building the Docker image, you would typically run 'mvn clean install'
COPY target/order-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that the Spring Boot application runs on
EXPOSE 8080

# Run the JAR file when the container launches
ENTRYPOINT ["java", "-jar", "app.jar"]
