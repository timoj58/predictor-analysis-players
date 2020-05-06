FROM openjdk:11-jdk
VOLUME /tmp
ADD predictor-analysis-players-*-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar", "/app.jar"]
