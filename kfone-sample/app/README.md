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
1. Change following properties in the `application.properties` file given in the `src/main/resources` folder.
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
4. You can host this `com.kfone.store.app-0.0.1-SNAPSHOT.jar` jar in any of hosting platforms
5. Change the `Authorized redirect URLs` of the application based on the hostname.

# How to integrate a spring-boot application with Asgardeo
If you want to know how to integrate your spring-boot application with Asgardeo, Refer [this](how-to-integrate.md)
