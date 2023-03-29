package com.example.demo.services;

import com.example.demo.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UtilService utilService;

    public User getUser(Authentication authentication) {
        User user = new User();
        if (authentication != null) {

            user.setAuthenticated(authentication.isAuthenticated());
            DefaultOidcUser userDetails = (DefaultOidcUser) authentication.getPrincipal();
            logger.info("User details: " + userDetails);

            user.setId(userDetails.getClaim("id"));
            user.setUsername(userDetails.getClaim("username"));
            String fullName = "";
            if (null != userDetails.getClaim("given_name")) {
                user.setFirstName(userDetails.getClaim("given_name"));
                fullName += userDetails.getClaim("given_name");
            }
            if (null != userDetails.getClaim("family_name")) {
                user.setLastName(userDetails.getClaim("family_name"));
                fullName += " " + userDetails.getClaim("family_name");
            }
            user.setFullName(fullName);
            String accessToken = utilService.getAccessToken((OAuth2AuthenticationToken) authentication);
            System.out.println("accessToken: " + accessToken);
            if (accessToken == null) {
                return null;
            }
            boolean emailVerified = utilService.getEmailVerifiedClaim(authentication, accessToken);
            logger.info("Email verified: " + emailVerified);
            user.setEmailVerified(emailVerified);
            user.setTier(userDetails.getClaim("tier"));
            return user;
        }

        user.setAuthenticated(false);
        return user;
    }
}
