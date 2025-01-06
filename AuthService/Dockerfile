FROM amazoncorretto:22
LABEL version="1.0"
EXPOSE 8087:8087
WORKDIR /app
COPY target/AuthService-0.0.1-SNAPSHOT.jar /app/AuthService-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","AuthService-0.0.1-SNAPSHOT.jar"]