ARG JDK_VERSION=17

FROM openjdk:${JDK_VERSION}-slim

WORKDIR /app
COPY ../loyalty-voucher/target/loyalty-voucher-0.0.1-SNAPSHOT.jar /app

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "loyalty-voucher-0.0.1-SNAPSHOT.jar"]
