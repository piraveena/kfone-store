# Register application
1. Follow this Asgardeo documentation to register an application: https://wso2.com/asgardeo/docs/guides/applications/register-oidc-web-app/#register-the-app
2. Configure Authorized redirect URLs based on your app's host name and port :
```
   Authorized redirect URLs <hostname>/login/oauth2/code/asgardeo, <hostname>/index
```
Example value if the app is running on `localhost:8080`:
```
   Authorized redirect URLs http://localhost:8080/login/oauth2/code/asgardeo, http://localhost:8080/index
```

# Pre-requisites
1. Java 11
2. Maven version above 3.5.0

# How to run this application locally

## Configure
1. Change following properties in the `application.properties` file
```
org.name=<add your org name>
client-id=<add your app's client id>
client-secret=<add your app's client secret>

```

Sample value:
```
org.name=kfoneorg
client-id=8n6x7jAj2tOF9a_x0offgE0XQEa
client-secret=8n6x7jAj2tee9a_x0oxKBgE0XQEa

```
  
## Run

Option1 :  Running using embedded tomcat server
1. Clone this project
2. Change the `application.properties` file as mentioned in the above section
3. In the terminal, run `mvn clean spring-boot:run`
4. In the browser, go to `http://localhost:8080/index`

Option2 :  Running as a executable jar
1. Clone this project
2. Change the `application.properties` file as mentioned in the above section
3. In the terminal, run `mvn clean install`
4. Go to `/target`
5. In the terminal, run `java -jar kfone-app-0.0.1-SNAPSHOT.jar`
6. In the browser, go to `http://localhost:8080/index`

# How to deploy this application in a production environment
1. Clone this project
2. Change the `application.properties` file as mentioned in the above section
3. In the terminal, run `mvn clean install`
4. You can host this `kfone-app-0.0.1-SNAPSHOT.jar` jar in any of hosting platforms
5. Change the `Authorized redirect URLs` of the application based on the hostname.

You can try out our sample application hosted in the following URL:
`https://kfone-app.fly.dev/`

# How to integrate a spring-boot application with Asgardeo

## Login

## logout (sign-in, sign-out)

## Invoke Asgardeo self-service APIs
To invoke Asgardeo self-service APIs, you need to get an access token. You can get an access token by following the steps mentioned in the following link:
https://wso2.com/asgardeo/docs/apis/authentication/#get-an-access-token

Note: To invoke the self-service APIs, the application you are using should be registered as a management application in Asgardeo.

## How to get user profile information
We utilized the `scim2/Me` endpoint of Asgardeo to get the user profile information. It is also part of the self-service APIs.
You can find more information about these endpoints in the following link:
https://wso2.com/asgardeo/docs/apis/scim2/#/paths/Me/get

1. We used the GET endpoint to fetch the user information from the Asgardeo server.
2. We used the PATCH endpoint to update the user information in the Asgardeo server.

Also we have utilized the `/userinfo` endpoint of the management application to get the user profile information.

## Logged in session

## How to maintain, state, session, session timeouts

## Securely store and use credentials in the application side

## How to deploy/host applications

## How to manage configuration
All the configuration related to the application is stored in the `application.properties` file. You can change the configuration values based on your requirements.
The configuration values are read from the `application.properties` file at the application startup. Please find our sample configuration file in the following link:
https://github.com/piraveena/kfone-store/blob/main/kfone-app/src/main/resources/application.properties
