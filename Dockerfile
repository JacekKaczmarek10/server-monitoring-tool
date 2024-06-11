FROM maven:3.9.6-amazoncorretto-21 AS build

ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD

ENV DB_URL=$DB_URL
ENV DB_USERNAME=$DB_USERNAME
ENV DB_PASSWORD=$DB_PASSWORD

WORKDIR /build
COPY . .
RUN mvn clean install

FROM openjdk:21
COPY --from=build /build/target/DockerMonitor-0.0.1-SNAPSHOT.jar /usr/local/lib/DockerMonitor-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar", "/usr/local/lib/DockerMonitor-0.0.1-SNAPSHOT.jar"]
