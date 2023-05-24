FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar stats-server.jar
#RUN #ln -sf /usr/share/zoneinfo/Europe/Paris /etc/localtime
ENTRYPOINT ["java","-jar","/stats-server.jar"]
