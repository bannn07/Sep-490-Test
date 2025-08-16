# =====================
# Stage 1: Build
# =====================
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven configuration first (for caching)
COPY pom.xml ./
COPY mvnw* ./
COPY .mvn ./.mvn

# Copy source code
COPY src ./src
COPY application*.properties ./

# Build JAR
RUN mvn clean package -DskipTests

# =====================
# Stage 2: Run
# =====================
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Run with default profile (prod)
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
