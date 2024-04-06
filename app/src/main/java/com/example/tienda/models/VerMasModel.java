package com.example.tienda.models;

import java.io.Serializable;

public class VerMasModel implements Serializable {
    String name;
    String description;
    String img_url;
    Double price;
    String type;

    public VerMasModel() {
    }

    public VerMasModel(String name, String description, String img_url, Double price, String type) {
        this.name = name;
        this.description = description;
        this.img_url = img_url;
        this.price = price;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}