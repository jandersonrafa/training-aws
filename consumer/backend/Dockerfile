FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY /target/consumer-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Djava.spring.profiles=prod","-jar","app.jar"]
