FROM maven:3.9.9-eclipse-temurin-11 AS build
WORKDIR /app

# Install the local graphqlweb jar before resolving project dependencies.
COPY pom.xml /app/pom.xml
COPY dependencies /app/dependencies
RUN mvn -q -DskipTests install:install-file \
  -Dfile=dependencies/graphqlweb-0.0.47-jar-with-dependencies.jar \
  -DgroupId=com.trilobiet \
  -DartifactId=graphqlweb \
  -Dversion=0.0.47 \
  -Dpackaging=jar

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
