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
package com.kfone.backend;

import com.kfone.backend.db.ProductDB;
import com.kfone.backend.model.AddDeviceRequest;
import com.kfone.backend.model.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class SimpleController {
    Logger logger = LoggerFactory.getLogger(SimpleController.class);

    @GetMapping("/products")
    public ResponseEntity<List<Device>> hello() {

        Map<String, Device> deviceMap = ProductDB.getInstance().getDb();
        return ResponseEntity.ok(new ArrayList<>(deviceMap.values()));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Device> getDevice(@PathVariable String id) {
        logger.info("id: " + id);

        Device device = ProductDB.getInstance().getDevice(id);
        return ResponseEntity.ok(device);
    }

    @PostMapping(value="/api/addProducts", produces = "application/json")
    ResponseEntity<Object> postAPI(@Validated @RequestBody AddDeviceRequest addDeviceRequest) {

        // This API is protected
        return ResponseEntity.ok().build();
    }
}
