#Build
FROM gradle:8.14.3-jdk-21-and-24 AS buildstage
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY shop/src ./src
RUN gradle clean build -x test

#Runtime
FROM openjdk:21
WORKDIR /app
COPY --from=buildstage /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]