FROM openjdk:8-jdk-alpine

LABEL version="2.2.6.RELEASE"

MAINTAINER Sim Wang <8966188@qq.com>

ENV ACTIVE test

ADD target/x7-demo-2.2.6.RELEASE.jar /data/deploy/demo/app.jar

#RUN apk add --no-cache curl

#CMD ["mkdir -p /data/logs"]
#VOLUME /data/logs /data/logs

EXPOSE 6661

ENTRYPOINT ["java","-Dspring.profiles.active=${ACTIVE}","-jar", "/data/deploy/demo/app.jar"]