FROM maven:3.9.9-eclipse-temurin-11 AS build
WORKDIR /app

# Cache dependencies first.
COPY pom.xml /app/pom.xml
COPY dependencies /app/dependencies
RUN mvn -q -DskipTests package -Pprod || true

# Build the application.
COPY src /app/src
COPY frontend /app/frontend
COPY css /app/css
RUN mvn -q -DskipTests package -Pprod

FROM eclipse-temurin:11-jre
WORKDIR /app

# Render provides $PORT; Spring will use it via the command.
COPY --from=build /app/target/*.jar /app/doabooks.jar

EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/doabooks.jar"]
