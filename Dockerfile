FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

#COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]
# F
# ROM eclipse-temurin:17-jdk-alpine
# COPY ./target/*.jar user-service.jar
# ENTRYPOINT ["java","-jar","user-service.jar"]