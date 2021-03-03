FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER river_015@163.com

ENV TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /river/river-auth

WORKDIR /river/river-auth

EXPOSE 3000

ADD ./target/river-auth.jar ./

CMD java -Djava.security.egd=file:/dev/./urandom -jar river-auth.jar
