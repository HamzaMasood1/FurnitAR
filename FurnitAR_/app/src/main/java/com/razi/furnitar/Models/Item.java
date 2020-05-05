package com.razi.furnitar.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Item implements Serializable {
    private Integer id;
    private String name;
    private Double price;
    private String image;
    private Integer isAR;
    private Integer quantity;
    private String description;

    public Item() {
    }

    public Item(Integer id, String name, Double price, String img, Integer isAR, String desc, Integer quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = img;
        this.isAR = isAR;
        this.description = desc;
        this.quantity = quantity;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setIsAR(Integer isAR) {
        this.isAR = isAR;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public Integer getIsAR() {
        return isAR;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }
}
