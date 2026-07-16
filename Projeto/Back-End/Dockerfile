FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Baixa dependencias em camada separada: rebuilds so recompilam o codigo.
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# MaxRAMPercentage usa a memoria real do container (o padrao da JVM e so ~25%).
ENTRYPOINT ["sh", "-c", "java -XX:MaxRAMPercentage=75.0 $JAVA_OPTS -jar app.jar"]