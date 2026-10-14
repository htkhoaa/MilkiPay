# ===============================================================
# Multi-stage Dockerfile for MilkiPay
# Stage 1: Build with Maven
# Stage 2: Minimal JRE runtime image
# ===============================================================

# ------------------------Build Stage----------------------------
FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Set working directory
WORKDIR /app

# Cache dependencies separately from source code
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Compile and package Spring Boot application
RUN mvn clean package -DskipTests -B

# ------------------------Runtime Stage----------------------------
FROM eclipse-temurin:21-jre AS runtime

# Set working directory
WORKDIR /app

# Copy executable JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot application port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]