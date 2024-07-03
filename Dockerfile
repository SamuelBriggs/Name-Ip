FROM maven:3.8.5-openjdk-22 AS build
COPY . .
RUN mvn install -DskipTests

FROM openjdk:22.0.1-jdk-slim
COPY --from=build /target/Hello-Client-0.0.1-SNAPSHOT.jar Hello-Client.jar

EXPOSE  8080
ENTRYPOINT ["java", "-jar","Hello-Client.jar"]






