ARG MVN_VERSION=3.8.3
ARG JDK_VERSION=11

FROM maven:${MVN_VERSION}-jdk-${JDK_VERSION}-slim as build

WORKDIR /build
COPY ../pom.xml .
COPY pom.xml ./cms/
COPY ../loyalty-core/pom.xml ./core/

# create a layer with all of the Maven dependencies, first time it takes a while consequent call are very fast
RUN mvn dependency:resolve-plugins

COPY ./pom.xml /tmp/
COPY ./src ./tmp/src/
WORKDIR /tmp/

# build project
RUN mvn clean install -U

#extract JAR layers
WORKDIR ./tmp/target
RUN java -Djarmode=layertools -jar *.jar extract

# runtime image
FROM gcr.io/distroless/java:${JDK_VERSION} as runtime

WORKDIR /tmp/target
EXPOSE 8080

# set entry point to layered spring boot application
ENTRYPOINT ["java", "-jar", "*.jar"]

