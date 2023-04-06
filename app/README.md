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
Configurations can be managed by using `application.properties` file or `application.yml` file which is shipped with spring-boot apps.
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

## Logout

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

## Get attributes from the ID token

You can get the email attributes from the ID token by using the following code snippet. Similary, get other attributes available in the ID token as `userDetails.getClaims().get("attribute_name")`. 


```java
private String getEmailFromIdToken(Authentication authentication) {

        logger.info("Getting email from id token");
        DefaultOidcUser userDetails = (DefaultOidcUser) authentication.getPrincipal();
        return (String) userDetails.getClaims().get("email");
    }
```

Once you add the attributes, call this method from your controller class to get the attributes.

```java
String email = getEmailFromIdToken(authentication);
```

## Invoke Asgardeo self-service APIs

To invoke Asgardeo self-service APIs, you need to get an access token. 

### Getting access token

You can get the access token by using the following code snippet, and pass the `authentication` object to the method.
```java
    
  private String getAccessToken(OAuth2AuthenticationToken authentication) {

        String clientRegistrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        logger.info(clientRegistrationId);
        OAuth2AuthorizedClient
                oauthclient = authorizedClientService.loadAuthorizedClient(clientRegistrationId, authentication.getName());
        if (oauthclient == null) {
            return null;
        }
        OAuth2AccessToken accessToken = oauthclient.getAccessToken();
        if (accessToken == null) {
            return null;
        } else {
            logger.info("Access token:" + accessToken.getTokenValue());
            return accessToken.getTokenValue();
        }
    }
```

### Use RestTemplate to invoke Asgardeo self-service APIs

```java

@Autowired
private RestTemplate restTemplate;

public  JSONObject callGetAPI(String url, String accessToken) throws Exception {

        logger.info("Calling GET Operation of the REST API : " + url);
        return callAPI(url,"GET", accessToken, null);
}
        
private JSONObject callAPI(String url, String operation, String accessToken, String body) throws Exception{

        ResponseEntity<String> restApi;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(accessToken);

        if (StringUtils.equalsIgnoreCase(operation, "GET")) {
            HttpEntity<String> request = new HttpEntity<>(headers);
            restApi = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } else if (StringUtils.equalsIgnoreCase(operation, "POST")) {
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            restApi = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        } else {
            HttpEntity<String> request = new HttpEntity<>(headers);
            restApi = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        }

        int statusCode = restApi.getStatusCode().value();
        if (statusCode == 200) {
            String response = restApi.getBody();
            return new JSONObject(response);
        } else if (statusCode == 401) {
            logger.info("Unauthorized access");
            return new JSONObject("{\"error\": \"Unauthorized access\"}");
        } else {
            logger.info("Error occurred while calling the API");
            throw new Exception("Error occurred while calling the API");
        }
    }
```

Now, you can call the method `callGetAPI` with `accessToken` and the `API endpoint` to get the response from Asgardeo self-service API.

## How to get user profile information

There are several ways to get user profile information:
1. [Use ID token to get user information](#get-attributes-from-the-id-token)
2. Use Asgardeo `scim2/Me` API to get user information
3. Use Asgardeo `/userinfo` API to get user information

### Use Asgardeo `scim2/Me` API to get user information
1. Refer [Invoke Asgardeo self-service APIs](#invoke-asgardeo-self-service-apis) to get the access token and call the self-service APIs.
2. Pass the scimMe API URL and the obtained accessToken into `getScimeMe`method to get the user profile information.

```java
public JSONObject getScimeMe(String scimeMeURL, String accessToken) throws LoginException {

        JSONObject response = callGetAPI(scimeMeURL,accessToken)
    }
```
### Use Asgardeo `/userinfo` API to get user information

1. Refer [Invoke Asgardeo self-service APIs](#invoke-asgardeo-self-service-apis) to get the access token and call the userinfo API.
2. Pass the userinfo API URL and the obtained accessToken into `getUserInfo`method to get the user profile information.

```java
public JSONObject getUserInfo(String userinfoURL, String accessToken) throws LoginException {

        JSONObject response = callGetAPI(userInfo,accessToken)
    }
```
