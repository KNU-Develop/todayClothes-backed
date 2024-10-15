FROM amazoncorretto:17-alpine-jdk
RUN apk add --no-cache python3 py3-pip
COPY remove_bg.py /app/scripts/remove_bg.py
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
