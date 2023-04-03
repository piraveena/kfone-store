# KFone Spring-boot Boilerplate App

This is a simple sample application that demonstrates how to integrate a spring-boot application with Asgardeo.

## Prerequisites

### Required Java and Maven versions
1. Java 11
2. Maven version above 3.5.0

### Register application in Asgardeo
1. Follow this Asgardeo documentation to register an application: https://wso2.com/asgardeo/docs/guides/applications/register-oidc-web-app/#register-the-app
2. Configure Authorized redirect URLs based on your app's host name and port :
```
   Authorized redirect URLs <hostname>/login/oauth2/code/asgardeo, <hostname>/index
```
Example value if the app is running on `localhost:8080`:
```
   Authorized redirect URLs http://localhost:8080/login/oauth2/code/asgardeo, http://localhost:8080/index
```

### Configurations to be added to the application.properties file

Replace org_name, client_id and client_secret with the values obtained from the Asgardeo application registration.
```
org.name=<add your org name>
client-id=<add your app's client id>
client-secret=<add your app's client secret>
```

### Configure resource server
A resource server application has been already hosted with fly.io. You can use your own resource server by changing the following configurations in the application.properties file.
```
resource-server-url=<add your resource server url>
```

## Run the application locally
Since, the spring-boot application contains Spring devTools as a dependency, you can run the application easily with the help of the embedded tomcat server.
1. Clone this project
2. Change the `application.properties` file as mentioned in the above section
3. In the terminal, run `mvn clean spring-boot:run`
4. In the browser, go to `http://localhost:8080/index`
