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

## Before you start
Following dependencies need to be added into the `pom.xml` file:
1. spring-boot-starter-oauth2-client
2. spring-boot-starter-security
3. spring-boot-starter-web
4. spring-boot-starter-thymeleaf


## Login

Once you add the above dependencies, Add below properties in the `application.properties` file. It will automatically integrate login capabilities to your app.
```
spring.security.oauth2.client.registration.asgardeo.client-name=Asgardeo
spring.security.oauth2.client.registration.asgardeo.client-id=${client-id}
spring.security.oauth2.client.registration.asgardeo.client-secret=${client-secret}
spring.security.oauth2.client.registration.asgardeo.redirect-uri={baseUrl}/login/oauth2/code/asgardeo
spring.security.oauth2.client.registration.asgardeo.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.asgardeo.scope=openid, address, phone, profile, internal_login
```

Spring Boot has a default login page. All the endpoints of the application are secured except the `/login` page. You can customize your login page or redirect to Asgardeo login page without showing any login page in your app.

### Remove the default “/login” page and redirect directly to Asgardeo login page
1. Add below properties in the controller class.

```java
@GetMapping("/login")
public String getLoginPage(Model model) {
    return "redirect:/oauth2/authorization/wso2";
}
```

## logout (sign-in, sign-out)

1. Configure a ConfigSecurity class by extending WebSecurityConfigurerAdapter.

```java
@EnableWebSecurity
public class ConfigSecurity extends WebSecurityConfigurerAdapter {

protected void configure(HttpSecurity http) throws Exception {

       http.authorizeRequests()
                    .antMatchers("/login")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
               .oauth2Login()
                    .loginPage("/login")
               .and()
               .logout()
                    .logoutSuccessHandler(oidcLogoutSuccessHandler());

}

@Autowired
private ClientRegistrationRepository clientRegistrationRepository;

private LogoutSuccessHandler oidcLogoutSuccessHandler() {
OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
new OidcClientInitiatedLogoutSuccessHandler(
this.clientRegistrationRepository);

       oidcLogoutSuccessHandler.setPostLogoutRedirectUri(
               URI.create("http://localhost:8080/")); //Need to give the post-rediret-uri here

       return oidcLogoutSuccessHandler;
}
}
```

2. Add the /logout redirection when user clicks the Logout button.

```html
<div style="float:right">
   <form method="post" th:action="@{/logout}"  class="navbar-form navbar-right">
       <button id="logout-button" type="submit" class="btn btn-danger">Logout</button>
   </form>
</div>
```

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

Also we have utilized the `/userinfo` endpoint of the management application to get the user profile information. Find the implementation below.
```java
public JSONObject getUserInfo(String accessToken) throws LoginException {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(accessToken);
        logger.info(accessToken);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<String> restApi = restTemplate.exchange(userInfoEndpoint, HttpMethod.GET, entity, String.class);
        int statusCode = restApi.getStatusCode().value();
        logger.info(statusCode + "");
        if (statusCode == 200) {
            String response = restApi.getBody();
            logger.info(response);
            return new JSONObject(response);
        } else {
            logger.info("Error occurred while calling the API");
            throw new LoginException("Error occurred while calling the API");
        }
    }
```

## Logged in session

## How to manage configuration
Configurations can be managed by using `application.properties` file or `application.yml` file which is shipped with spring-boot apps.
