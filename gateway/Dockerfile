FROM amazoncorretto:11-alpine-jdk
COPY gateway/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
