ARG JDK_VERSION=17

FROM openjdk:${JDK_VERSION}-slim

WORKDIR /app
COPY ../loyalty-cms/target/loyalty-cms-0.0.1-SNAPSHOT.jar /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "loyalty-cms-0.0.1-SNAPSHOT.jar"]

