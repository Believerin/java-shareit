FROM amazoncorretto:11-alpine-jdk
COPY server/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
