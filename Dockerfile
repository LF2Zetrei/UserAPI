# Étape 1 : builder le jar
FROM gradle:8-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar

# Étape 2 : image finale
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
