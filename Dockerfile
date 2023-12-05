FROM maven:3.9.5-eclipse-temurin-17-alpine AS MAVEN_BUILD

COPY ./ ./

RUN mvn clean package

FROM eclipse-temurin:17-jdk-jammy

COPY --from=MAVEN_BUILD /com-boot-user-service/target/user-service-1.0.jar /user-service.jar

# set the startup command to execute the jar
CMD ["java", "-jar", "/user-service.jar"]