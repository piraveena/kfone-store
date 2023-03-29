package com.example.demo.services;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class UtilService {
    Logger logger = LoggerFactory.getLogger(UtilService.class);

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private ProductService productService;


    @Value("${asgardeo.scim.me.endpoint}")
    private String scimMeEndpoint;


    @Autowired
    RestTemplate restTemplate;

    public String getAccessToken(OAuth2AuthenticationToken authentication) {

        String clientRegistrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        logger.info(clientRegistrationId);
        OAuth2AuthorizedClient oauthclient = authorizedClientService.loadAuthorizedClient(clientRegistrationId, authentication.getName());
        if (oauthclient == null) {
            return null;
        }
        OAuth2AccessToken accessToken = oauthclient.getAccessToken();
        if (accessToken == null) {
            return null;
        } else {
            return accessToken.getTokenValue();
        }
    }

    public boolean getEmailVerifiedClaim(Authentication authentication, String accessToken) {

        logger.info("getEmailVerifiedClaim");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        JSONObject response = callGetAPI(scimMeEndpoint, entity);
        JSONObject customSchemaAttributes = (JSONObject)response.get("urn:scim:wso2:schema");
        logger.info(customSchemaAttributes.toString());
        if (!customSchemaAttributes.has("emailVerified") || customSchemaAttributes.get("emailVerified") == null) {
            return false;
        }
        String emailVerified = (String) customSchemaAttributes.get("emailVerified");
        return Boolean.parseBoolean(emailVerified);
    }

    public String getUserTier(Authentication authentication, String accessToken) {

        logger.info("GetUserTier");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        JSONObject response = callGetAPI(scimMeEndpoint, entity);
        JSONObject customSchemaAttributes = (JSONObject)response.get("urn:scim:wso2:schema");
        logger.info(customSchemaAttributes.toString());
        if (!customSchemaAttributes.has("tier") || customSchemaAttributes.get("tier") == null) {
            return null;
        }
        return (String) customSchemaAttributes.get("tier");
    }

    public JSONObject callGetAPI(String url, HttpEntity<String> entity) {

        logger.info("get api");
        ResponseEntity<String> restApi = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        int statusCode = restApi.getStatusCode().value();
        logger.info(statusCode + "");
        if (statusCode == 200) {
            String response = restApi.getBody();
            logger.info(response);
            return new JSONObject(response);
        }
        return null;
    }
}
