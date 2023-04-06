package com.kfone.boilerplate;

import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.thymeleaf.util.StringUtils;

@Controller
class AppController {

    Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private APIService apiService;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Value("${asgardeo.scim.me.endpoint}")
    private String scimMeEndpoint;

    @Value("${resource.server.url}")
    private String resourceServerUri;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/login")
    public String getLoginPage(Model model) {

        logger.info("Rendering login page");
        // This will redirect to Asgardeo login page
        return "redirect:/oauth2/authorization/asgardeo";
    }

    @GetMapping({"/index", "/"})
    public String getHomePage(Model model, Authentication authentication) {

        if (authentication == null) {
            model.addAttribute("isAuthenticated", false);
            return "index";
        }

        String email = getEmailFromIdToken(authentication);
        model.addAttribute("userName", email);
        model.addAttribute("isAuthenticated", authentication.isAuthenticated());

        String accessToken = getAccessToken((OAuth2AuthenticationToken) authentication);
        logger.info("Access token: " + accessToken);
        if (StringUtils.isEmpty(accessToken)) {
            return "redirect:/login";
        }

        // make a GET call to the SCIM Me endpoint
        scimMeAPICall(accessToken, model);

        // resource server sample API call
        // resourceServerAPICall(accessToken, model);

        return "index";
    }

    private String getEmailFromIdToken(Authentication authentication) {

        logger.info("Getting email from id token");
        DefaultOidcUser userDetails = (DefaultOidcUser) authentication.getPrincipal();
        return (String) userDetails.getClaims().get("email");
    }

    /**
     * This method is used to get the access token from the OAuth2AuthorizedClientService.
     *
     * @param authentication OAuth2AuthenticationToken
     * @return Access token
     */
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


    /**
     * This method is used to call the SCIM Me endpoint.
     *
     * @param accessToken Access token.
     * @param model       Model.
     */
    private void scimMeAPICall(String accessToken, Model model) {

        JSONObject response = null;
        try {
            response = apiService.callGetAPI(scimMeEndpoint, accessToken);
            model.addAttribute("scimMe", response.toString());
        } catch (Exception e) {
            logger.error("Error occurred while calling the API", e);
        }
    }

    /**
     * This method is used to call the resource server sample API.
     *
     * @param accessToken Access token.
     * @param model       Model.
     */
    private void resourceServerAPICall(String accessToken, Model model) {

        String url = resourceServerUri + "/api/addProducts";
        try {
            JSONObject response = apiService.callPOSTAPI(url, accessToken, getRequestBody().toString());
            model.addAttribute("resourceServerAPIStatusCode", response.toString());
        } catch (Exception e) {
            logger.error("Error occurred while calling the API", e);
        }
    }

    private JSONObject getRequestBody() {

        JSONObject object = new JSONObject();
        object.put("name", "test");
        object.put("id", UUID.randomUUID());
        return object;
    }
}
