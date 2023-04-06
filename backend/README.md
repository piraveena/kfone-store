# Pre-requisites
1. Java 11
2. Maven version above 3.5.0

# How to build resource server for production
1. In the terminal, run `mvn clean install`
2. In the terminal, run `java -jar demo-0.0.1-SNAPSHOT.jar --spring.config.location=application.properties`

# How to test this application for development using embedded server.
1. In the terminal, run `mvn clean spring-boot:run`
2. In the terminal, Run `curl localhost:8081/ -H "Authorization: Bearer <access_token>"`

Production server endpoint: `https://spring-resource-server.fly.dev`

To call the protected endpoint:

```
curl --location 'localhost:8081/api' \
--header 'Authorization: Bearer 87302457-7fb0-3b7c-a4e4-be95d12a14c3' \
--header 'Cookie: JSESSIONID=E147FF8A47D4DD3D7BA0A09C03A33B2C'
```


To call the unprotected endpoint,

```
curl --location 'localhost:8081/products' \
```


References:
https://docs.spring.io/spring-security/reference/5.6.0-RC1/servlet/oauth2/oauth2-resourceserver.html#oauth2resourceserver-opaque-minimaldependencies
