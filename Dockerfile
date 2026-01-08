# --- Stage 1 : Build & Test ---
FROM maven:3.9-eclipse-temurin-17 AS build
LABEL authors="jbfro"
WORKDIR /app

# 1. On copie le Jar TarsosDSP DANS l'image Docker
COPY TarsosDSP-2.4.jar .

# 2. On l'installe manuellement DANS le Maven de l'image Docker
RUN mvn install:install-file -Dfile=TarsosDSP-2.4.jar -DgroupId=be.tarsos.dsp -DartifactId=core -Dversion=2.5 -Dpackaging=jar

# Ensuite on continue comme d'habitude...
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests=false

# --- Stage 2 : Runtime ---
# (Reste inchangé)
FROM eclipse-temurin:17-jre-alpine

# BUILD JAR FINAL
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV CENTRAL_HOST=samsoul-central
ENV CENTRAL_PORT=9999
ENV LISTEN_PORT=65432

EXPOSE 65432

ENTRYPOINT ["java", "-jar", "app.jar"]