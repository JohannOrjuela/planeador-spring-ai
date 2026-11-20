FROM gradle:9-jdk21 AS build
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN --mount=type=cache,target=/home/gradle/.gradle/caches,uid=1000,gid=1000 \
    --mount=type=cache,target=/home/gradle/.gradle/wrapper,uid=1000,gid=1000 \
    gradle clean test bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --create-home --shell /usr/sbin/nologin spring
COPY --from=build --chown=spring:spring /home/gradle/project/build/libs/*.jar app.jar
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

