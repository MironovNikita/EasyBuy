#Build
FROM gradle:8.14.3-jdk-21-and-24 AS buildstage
WORKDIR /app
COPY . .
RUN gradle clean build -x test

#Runtime
FROM openjdk:21
WORKDIR /app
COPY --from=buildstage /app/shop/build/libs/*.jar app.jar
ENTRYPOINT ["/wait-for-it.sh", "easy-buy-db:5432", "--timeout=60", "--strict", "--",
            "/wait-for-it.sh", "redis:6379", "--timeout=60", "--strict", "--",
            "/wait-for-it.sh", "keycloak:8080", "--timeout=120", "--strict", "--",
            "java", "-jar", "app.jar"]