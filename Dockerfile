# --- Stage 1 : Build & Test ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Copie de la dépendance manuelle
COPY TarsosDSP-2.4.jar .

# 2. Installation FORCEE dans le dépôt local de l'image Docker
# Note : Sous Linux (Docker), pas besoin des guillemets complexes de PowerShell
RUN mvn install:install-file -Dfile=TarsosDSP-2.4.jar \
    -DgroupId=be.tarsos.dsp \
    -DartifactId=core \
    -Dversion=2.5 \
    -Dpackaging=jar

# 3. Copie du reste et compilation
COPY pom.xml .
# Téléchargement des autres dépendances (Cucumber, etc.)
RUN mvn dependency:go-offline

COPY src ./src
# Compilation (Les tests sont exécutés ici)
RUN mvn package

# --- Stage 2 : Runtime (Image finale légère) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Récupération du JAR compilé
COPY --from=build /app/target/*.jar app.jar

# Configuration Environnement
ENV LISTEN_PORT=65432
EXPOSE 65432

ENTRYPOINT ["java", "-jar", "app.jar"]