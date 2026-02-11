FROM eclipse-temurin:25-jre

WORKDIR /app

COPY target/docker.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
