FROM amazoncorretto:22
LABEL version="1.0"
EXPOSE 8081:8081
WORKDIR /app
COPY target/notificationservice-0.0.1-SNAPSHOT.jar /app/notificationservice-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","notificationservice-0.0.1-SNAPSHOT.jar"]