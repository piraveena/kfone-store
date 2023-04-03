package com.example.demo;

import com.example.demo.exceptions.LoginException;
import java.util.Arrays;
import java.util.UUID;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
class AppController {

    Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Value("${asgardeo.scim.me.endpoint}")
    private String scimMeEndpoint;

    @Value("${resource.server.url}")
    private String resourceServerUri;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/home")
    public String currentUserName(Model model, Authentication authentication) {
        DefaultOidcUser userDetails = (DefaultOidcUser) authentication.getPrincipal();
        model.addAttribute("userName", userDetails.getName());
        model.addAttribute("IDTokenClaims", userDetails);
        model.addAttribute("isAuthenticated", authentication.isAuthenticated());
        return "home";
    }

    @GetMapping("/index")
    public String getHomePage(Model model, Authentication authentication) {

        if (authentication == null) {
            model.addAttribute("isAuthenticated", false);
            return "index";
        }

        DefaultOidcUser userDetails = (DefaultOidcUser) authentication.getPrincipal();
        model.addAttribute("userName", userDetails.getName());
        model.addAttribute("IDTokenClaims", userDetails);
        model.addAttribute("isAuthenticated", authentication.isAuthenticated());

        String accessToken = getAccessToken((OAuth2AuthenticationToken) authentication);
        model.addAttribute("accessToken", accessToken);

        // make a GET call to the SCIM Me endpoint
        scimMeAPICall(accessToken, model);

        // resource server sample API call
        resourceServerAPICall(accessToken, model);

        return "index";
    }

    @GetMapping("/")
    public String firstPage(Model model, Authentication authentication) {

        return "redirect:/index";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {

        logger.info("Rendering login page");
        return "redirect:/oauth2/authorization/asgardeo";
    }

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

    private JSONObject callGetAPI(String url, HttpEntity<String> entity) throws LoginException {

        logger.info("get api");
        ResponseEntity<String> restApi = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
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

    private void scimMeAPICall(String accessToken, Model model) {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(accessToken);
        logger.info(accessToken);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        JSONObject response = null;
        try {
            response = callGetAPI(scimMeEndpoint, entity);
            model.addAttribute("scimMe", response.toString());
        } catch (LoginException e) {
            logger.info("Error occurred while calling the API");
        }
    }

    private void resourceServerAPICall (String accessToken, Model model) {

        String url = resourceServerUri + "/api/addProducts";
        logger.info("Rendering post api:" + url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject object = new JSONObject();
        object.put("name", "test");
        object.put("id", UUID.randomUUID());
        logger.info("Rendering accessToken:" + accessToken);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<String>(object.toString(), headers);
        ResponseEntity<String> restApi = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        model.addAttribute("resourceServerAPIStatusCode", restApi.getStatusCode().value());
        logger.info("Rendering resourceServerAPIStatusCode:" + restApi.getStatusCode().value());
        logger.info(restApi.getBody());
    }
}
