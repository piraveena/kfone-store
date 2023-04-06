package com.kfone.app.models;

public class Product {
    private String id;
    private String name;
    private int price;
    private String description;
    private String manufacturer;

    private String image;

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

    public String toString() {
        return "Product: " + name + ", " + price + ", " + description + ", " + manufacturer + ", " + image;
    }
}
