FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw clean package

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
