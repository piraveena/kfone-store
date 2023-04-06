/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.org) All Rights Reserved.
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
package com.kfone.boilerplate;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Service
public class APIService {

    @Autowired
    private RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(APIService.class);

    public  JSONObject callGetAPI(String url, String accessToken) throws Exception {

        logger.info("Calling GET Operation of the REST API : " + url);
        return callAPI(url,"GET", accessToken, null);
    }

    public  JSONObject callPOSTAPI(String url, String accessToken, String body) throws Exception {

        logger.info("Calling POST Operation of the REST API : " + url);
        return callAPI(url,  "POST", accessToken, body);
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
}
