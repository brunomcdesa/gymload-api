FROM maven:3.8.6-openjdk-21 AS build
COPY . .
RUN mvn clean install

FROM openjdk:21-jdk-slim
EXPOSE 8080

COPY --from=build /target/gymload-api-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
