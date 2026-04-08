# Etapa 1: Build da aplicação
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests -q

# Etapa 2: Imagem final
FROM eclipse-temurin:21-jre-alpine

RUN adduser -D springuser

WORKDIR /app

COPY --from=build --chown=springuser:springuser /app/target/*.jar app.jar

USER springuser

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]