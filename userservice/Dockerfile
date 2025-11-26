# Stage 1: Making jar file from project files
FROM maven:3.9.11-eclipse-temurin-21 AS stage1
# Set the working directory in the container
# WORKDIR /app
# Copy the pom.xml and the project files to the container
COPY pom.xml .
COPY src ./src
# Build the application using Maven
RUN mvn clean package -DskipTests
# Use an official OpenJDK image as the base image

# Stage 2: Copying jar file from Stage 1 and creating application  
FROM eclipse-temurin:21-jdk-alpine
# Set the working directory in the container
# WORKDIR /app
# Copy the built JAR file from the previous stage to the container
COPY --from=stage1 /target/userservice-0.0.1-SNAPSHOT.jar /userservice.jar

# update PATH environment variable
ENV MY_SECRET_KEY=U29tZXRoaW5nLXN0YXJ0cy1vbi10aW1lLWFuZC1uZXZlci1jb21lcy1iYWNr

EXPOSE 8080

# Set the command to run the application
CMD ["java", "-jar", "userservice.jar"]