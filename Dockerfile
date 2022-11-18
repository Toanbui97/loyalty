ARG MVN_VERSION=3.8.3
ARG JDK_VERSION=11

FROM maven:${MVN_VERSION}-jdk-${JDK_VERSION}-slim as build

WORKDIR /build
COPY pom.xml .
COPY ./loyalty-cms/pom.xml ./loyalty-cms/
COPY ./loyalty-core/pom.xml ./loyalty-core/
COPY ./loyalty-migration/pom.xml ./loyalty-migration/
COPY ./loyalty-voucher/pom.xml ./loyalty-voucher/

# create a layer with all of the Maven dependencies, first time it takes a while consequent call are very fast
RUN mvn dependency:go-offline

COPY ./pom.xml /tmp/

COPY ./loyalty-cms/pom.xml /tmp/loyalty-cms/
COPY ./loyalty-core/pom.xml /tmp/loyalty-core/
COPY ./loyalty-migration/pom.xml /tmp/loyalty-migration/
COPY ./loyalty-voucher/pom.xml /tmp/loyalty-voucher/

COPY ./loyalty-cms/src /tmp/loyalty-cms/src/
COPY ./loyalty-core/src /tmp/loyalty-core/src/
COPY ./loyalty-migration/src /tmp/loyalty-migration/src/
COPY ./loyalty-voucher/src /tmp/loyalty-voucher/src/

WORKDIR /tmp/

# build project
RUN mvn clean install -DskipTests

WORKDIR /tmp/loyalty-cms/
#extract JAR layers
RUN java -Djarmode=layertools -jar ./target/loyalty-cms-0.0.1-SNAPSHOT.jar extract
RUN echo $(ls -1 /tmp/loyalty-cms/)
# runtime image
#FROM openjdk:11 as runtime
FROM gcr.io/distroless/java:${JDK_VERSION} as runtime

WORKDIR /app

COPY --from=build /tmp/loyalty-cms/dependencies/ .
COPY --from=build /tmp/loyalty-cms/snapshot-dependencies/ .
COPY --from=build /tmp/loyalty-cms/spring-boot-loader/ .
COPY --from=build /tmp/loyalty-cms/application/ .

# copy layes from build image to runtime image as nonroot user
EXPOSE 8080

# set entry point to layered spring boot application
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

