FROM openjdk:17-jdk

RUN mkdir -p /app/db

COPY build/libs/Manager-*.jar /app/app.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "app.jar"]