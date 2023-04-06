package com.kfone.backend.model;

public class AddDeviceRequest {
    private String name;
    private String id;

    public AddDeviceRequest(String name, String id) {
        this.name = name;
        this.id = id;
    }

    //Getters
    public String getName() {

        return name;
    }

}
