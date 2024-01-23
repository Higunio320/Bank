FROM maven:3.9.5-amazoncorretto-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src/ src/

RUN mvn clean install -DskipTests

FROM amazoncorretto:17-alpine

WORKDIR /bank-app

COPY --from=build /app/target/*.jar app.jar

ARG KEYSTORE_PASSWORD

RUN keytool -genkeypair \
    -alias selfsigned_localhost_sslserver \
    -keyalg RSA \
    -keysize 2048 \
    -dname "CN=localhost,OU=ID,O=dummy,L=Gummy,S=Bear,C=PL" \
    -storetype PKCS12 \
    -keystore keystore.p12 \
    -validity 3650 \
    -storepass ${KEYSTORE_PASSWORD}

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]