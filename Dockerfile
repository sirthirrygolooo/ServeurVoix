FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY TarsosDSP-2.4.jar .

RUN mvn install:install-file -Dfile=TarsosDSP-2.4.jar \
    -DgroupId=be.tarsos.dsp \
    -DartifactId=core \
    -Dversion=2.5 \
    -Dpackaging=jar

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV LISTEN_PORT=65432
EXPOSE 65432

ENTRYPOINT ["java", "-jar", "app.jar"]