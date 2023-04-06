package com.kfone.backend.db;

import com.kfone.backend.model.Device;

import java.util.HashMap;
import java.util.Map;

public class ProductDB {
    private static ProductDB instance = null;

    private Map<String, Device> db = new HashMap<>();

    private ProductDB() {
    }

    public static ProductDB getInstance() {
        if (instance == null) {
            instance = new ProductDB();
            instance.addDevice(new Device("1", "iPhone 12", 999, "The iPhone 12 is the latest iPhone model", "Apple", "https://img.freepik.com/free-photo/phone-screen-with-abstract-marble-aesthetic_53876-145553.jpg"));
            instance.addDevice(new Device("2", "iPhone 12 Pro", 1099, "The iPhone 12 Pro is the latest iPhone model", "Apple", "https://img.freepik.com/free-photo/phone-screen-with-abstract-marble-aesthetic_53876-145553.jpg"));
            instance.addDevice(new Device("3", "iPhone 12 Pro Max", 1199, "The iPhone 12 Pro Max is the latest iPhone model", "Apple", "https://img.freepik.com/free-photo/phone-screen-with-abstract-marble-aesthetic_53876-145553.jpg"));
            instance.addDevice(new Device("4", "iPhone 12 Mini", 899, "The iPhone 12 Mini is the latest iPhone model", "Apple", "https://img.freepik.com/free-photo/phone-screen-with-abstract-marble-aesthetic_53876-145553.jpg"));
            instance.addDevice(new Device("5", "iPhone 11", 699, "The iPhone 11 is the latest iPhone model", "Apple", "https://img.freepik.com/free-photo/phone-screen-with-abstract-marble-aesthetic_53876-145553.jpg"));
        }
        return instance;
    }

    public void addDevice(Device device) {
        db.put(device.getId(), device);
    }

    public Device getDevice(String id) {
        return db.get(id);
    }

    public Map<String, Device> getDb() {
        return db;
    }
}
