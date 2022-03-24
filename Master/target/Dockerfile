FROM alpine
RUN apk add docker

FROM openjdk:8-jre-alpine

WORKDIR application
COPY lib lib
COPY Master.jar Master.jar

ENTRYPOINT ["/usr/bin/java", "-jar", "Master.jar"]
EXPOSE 8100