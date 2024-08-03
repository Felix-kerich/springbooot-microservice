# Use a base image with Java runtime
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/order-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8081

# Set the entry point
ENTRYPOINT ["java", "-jar", "app.jar"]
