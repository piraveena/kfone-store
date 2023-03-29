# kfone-store

This kfone has two components:
1. kfone-app: Front facing application
2. kfone-backend: Server that has APIs required by the front facing application

Both kfone-app and kfone-backend are spring-boot applications.

## Kfone app
To see how to run the app, refer: [kfone-app/README.md](kfone-app/README.md)
kfone-app is a web application, and it uses the following technologies:
1. Spring Boot and java
2. spring-security
3. spring-boot-starter-oauth2-client
4. embedded-tomcat

## Kfone backend
To see how to run the backend app, refer: [kfone-backend/README.md](kfone-backend/README.md)
1. Spring Boot and java
2. spring-boot-starter-oauth2-resource-server

3. Backend Server is hosted using fly.io