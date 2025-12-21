# Stage 1: Build the application
FROM gradle:8.14.3-jdk21 AS build
WORKDIR /home/gradle/src
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle
COPY src ./src
RUN ./gradlew bootJar --no-daemon

# Stage 2: Create the final image
# 최종 이미지 크기 최소화 (멀티 스테이지 빌드)
FROM eclipse-temurin:21-jre-jammy AS final
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
