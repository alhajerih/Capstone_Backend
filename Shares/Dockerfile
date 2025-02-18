# FROM amazoncorretto:22
# LABEL version="1.0"
# EXPOSE 8080:8080
# WORKDIR /app
# COPY target/Shares-0.0.1-SNAPSHOT.jar /app/Shares-0.0.1-SNAPSHOT.jar

# ENTRYPOINT ["java","-jar","Shares-0.0.1-SNAPSHOT.jar"]

# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn install -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your application runs on
EXPOSE 8080:8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]