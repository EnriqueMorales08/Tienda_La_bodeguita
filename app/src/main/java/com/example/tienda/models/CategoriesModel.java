package com.example.tienda.models;

public class CategoriesModel {
    String name;
    String descripcion;
    String rating;
    String type;
    String img_url;

    public CategoriesModel() {
    }

    public CategoriesModel(String name, String descripcion, String rating, String type, String img_url) {
        this.name = name;
        this.descripcion = descripcion;
        this.rating = rating;
        this.type = type;
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
