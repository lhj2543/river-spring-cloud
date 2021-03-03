FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER river_015@163.com

ENV TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /river/river-gateway

WORKDIR /river/river-gateway

EXPOSE 9999

ADD ./target/river-gateway.jar ./

CMD java -Djava.security.egd=file:/dev/./urandom -jar river-gateway.jar
