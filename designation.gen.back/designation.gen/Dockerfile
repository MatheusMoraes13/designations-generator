FROM eclipse-temurin:21-jre-alpine

WORKDIR /clh-manager/back-end

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]