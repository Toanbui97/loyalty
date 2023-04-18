ARG JDK_VERSION=17

FROM openjdk:${JDK_VERSION}-slim

WORKDIR /app
COPY ../loyalty-transaction/target/loyalty-transaction-0.0.1-SNAPSHOT.jar /app

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "loyalty-transaction-0.0.1-SNAPSHOT.jar"]

