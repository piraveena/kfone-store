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
package com.kfone.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@EnableWebSecurity
public class ConfigSecurity extends WebSecurityConfigurerAdapter {

    @Value( "${spring.security.oauth2.client.registration.asgardeo.post-logout-redirect-uri}" )
    private String postLogoutRedirectUri;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    Logger logger = LoggerFactory.getLogger(ConfigSecurity.class);

    protected void configure(HttpSecurity http) throws Exception {

        logger.info("Configuring security");
        http.authorizeRequests()
                .antMatchers( "/index", "/product/{id}", "/login", "/")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login().loginPage("/login")
                .and().logout()
                .logoutSuccessHandler(oidcLogoutSuccessHandler());

    }

    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(
                        this.clientRegistrationRepository);

        oidcLogoutSuccessHandler.setPostLogoutRedirectUri(postLogoutRedirectUri);
        return oidcLogoutSuccessHandler;
    }
}
