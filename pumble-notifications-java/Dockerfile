FROM maven:3.8-eclipse-temurin-18 AS build
ADD pom.xml .
ADD configure-maven.sh .
ADD src ./src

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

RUN ./configure-maven.sh $GITHUB_USERNAME $GITHUB_TOKEN
RUN mvn clean package

CMD java -jar ./target/pumble-notifications-1.0.0-shaded.jar