FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /opt/app

# Копируем библиотеку в контейнер
COPY lib/role-check-lib-1.0.0.jar /opt/lib/
RUN mvn install:install-file -Dfile=/opt/lib/role-check-lib-1.0.0.jar -DgroupId=com.project -DartifactId=role-check-lib -Dversion=1.0.0 -Dpackaging=jar

COPY mvnw pom.xml ./
COPY src ./src
RUN mvn clean install -DskipTests


FROM eclipse-temurin:17-jre-jammy
WORKDIR /opt/app
EXPOSE 8082
COPY --from=builder /opt/app/target/*.jar /opt/app/app.jar
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]