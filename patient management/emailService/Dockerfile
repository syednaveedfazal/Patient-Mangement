FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

# 💥 THIS will show full logs and prevent hiding error
RUN mvn clean package -DskipTests -e -X

FROM openjdk:21-slim AS runner
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 9009

ENTRYPOINT ["java", "-jar", "app.jar"]
