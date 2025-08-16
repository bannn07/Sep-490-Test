# =====================
# Stage 1: Build
# =====================
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy file cấu hình Gradle trước (tận dụng cache)
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle

# Copy source code
COPY src ./src
COPY application*.properties ./

# Build JAR
RUN ./gradlew bootJar --no-daemon

# =====================
# Stage 2: Run
# =====================
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy JAR từ stage build
COPY --from=build /app/build/libs/*.jar app.jar

# Chạy với profile mặc định (prod)
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
