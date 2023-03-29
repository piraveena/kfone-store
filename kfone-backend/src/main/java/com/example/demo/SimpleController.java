package com.example.demo;

import com.example.demo.db.ProductDB;
import com.example.demo.model.AddDeviceRequest;
import com.example.demo.model.Device;
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

        Device device = ProductDB.getInstance().getDevice(id);
        return ResponseEntity.ok(device);
    }

    @PostMapping(value="/api/addProducts", produces = "application/json")
    ResponseEntity<Object> postAPI(@Validated @RequestBody AddDeviceRequest addDeviceRequest) {

        // This API is protected
        return ResponseEntity.ok().build();
    }
}
