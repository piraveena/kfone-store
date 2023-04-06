package com.example.demo.model;

public class Device {
    private String id;
    private String name;
    private int price;
    private String description;
    private String manufacturer;

    private String image;

    public Device(String id, String name, int price, String description, String manufacturer, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.manufacturer = manufacturer;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getImage() {
        return image;
    }
}
