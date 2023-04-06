/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.kfone.app.services;

import com.kfone.app.models.User;
import com.kfone.app.exceptions.LoginException;
import org.json.JSONObject;
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

            String accessToken = utilService.getAccessToken((OAuth2AuthenticationToken) authentication);
            System.out.println("accessToken: " + accessToken);
            if (accessToken == null) {
                return null;
            }
            JSONObject userInfo = null;
            try {
                userInfo = utilService.getUserInfo(accessToken);
                logger.info("User info: " + userInfo.toString());
            } catch (LoginException e) {
                return null;
            }
            if (userInfo != null) {
                user.setUsername(userInfo.get("username").toString());
                String fullName2 = userInfo.get("given_name") + " " + userInfo.get("family_name");
                user.setFullName(fullName2);
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
