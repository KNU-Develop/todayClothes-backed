FROM amazoncorretto:17-alpine-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.security.egd=file:/dev/./urandom", "-jar", "app.jar"]