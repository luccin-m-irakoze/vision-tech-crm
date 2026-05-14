# =============================================================================
# Stage 1 — Build the Spring Boot fat-JAR with Maven on a JDK image.
# =============================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -e -DskipTests package

# =============================================================================
# Stage 2 — Slim runtime image with JRE only.
# =============================================================================
FROM eclipse-temurin:17-jdk-alpine AS runtime
WORKDIR /app

RUN apk add --no-cache wget

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=20s --timeout=5s --retries=5 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 0

ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
