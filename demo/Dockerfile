FROM openjdk:8-jdk-alpine

LABEL version="2.7.0"

MAINTAINER Sim Wang <8966188@qq.com>

ENV ACTIVE test
ENV JAVA_OPTS -Xms1024m -Xmx1024m -Xmn320m

ADD target/x7-demo-2.7.0.jar /data/deploy/demo/app.jar

#RUN apk add --no-cache curl
#/usr/lib/jvm/java-1.8-openjdk/jre/lib/fonts/fallback/simsun.ttc

VOLUME /data/logs /data/logs

EXPOSE 6661

YPOINT ["/bin/sh","-c","java ${JAVA_OPTS} -Dspring.profiles.active=${ACTIVE} -jar /data/deploy/demo/app.jar"]