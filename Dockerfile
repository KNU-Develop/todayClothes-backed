FROM amazoncorretto:17-alpine-jdk

# 한국 시간대 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Spring Boot 애플리케이션 JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 애플리케이션 시작 명령
ENTRYPOINT ["java", "-Dspring.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
