FROM maven:3.9.9-amazoncorretto-21 AS build
COPY . .
RUN mvn clean install

FROM amazoncorretto:21
EXPOSE 8080

COPY --from=build /target/gymload-api-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
