FROM openjdk:8-jre-alpine

WORKDIR application
COPY lib lib
COPY Node.jar Node.jar

ENTRYPOINT ["/usr/bin/java", "-jar", "Node.jar"]
EXPOSE 5000

