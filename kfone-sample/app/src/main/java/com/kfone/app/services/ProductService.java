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

import com.kfone.app.models.Product;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Value("${resource.server.url}")
    private String resourceServerUri;

    @Autowired
    RestTemplate restTemplate;


    public List<Product> getProducts() {
        String url = resourceServerUri + "/products";
        RestTemplate restTemplate = new RestTemplate();
        Product[] products = restTemplate.getForObject(url, Product[].class);

        if (products == null) {
            return List.of();
        }
        return Arrays.asList(products);
    }

    public Product getProduct(String id) {
        String url = resourceServerUri + "/products/" + id;
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(url, Product.class);
    }

    public int addProduct(String accessToken) {

        String url = resourceServerUri + "/api/addProducts";
        logger.info("Rendering post api:" + url );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        JSONObject object = new JSONObject();
        object.put("name", "test");
        object.put("id", UUID.randomUUID());
        logger.info("Rendering accessToken:" + accessToken );
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request =
                new HttpEntity<String>(object.toString(), headers);

        ResponseEntity<String> restApi = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return restApi.getStatusCode().value();
    }

}
