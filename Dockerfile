# Stage 1: Build the application
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app

# Copy maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw ./
COPY pom.xml ./

# Ensure mvnw has execute permissions and fix potential CRLF issues
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Download dependencies to cache them
RUN ./mvnw dependency:go-offline

# Copy application source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
