FROM openjdk:17-jdk-alpine

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=target/demo-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/test", "-Dspring.datasource.username=postgres", "-Dspring.datasource.password=P@ssw0rd", "-Dexchangerate.apikey=97545c22317948e32e67f5ea", "-jar",  "app.jar"]

#sudo docker run -d --network=host demo:1.0