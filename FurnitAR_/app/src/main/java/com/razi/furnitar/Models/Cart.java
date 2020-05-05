package com.razi.furnitar.Models;

public class Cart {
    private Integer userid;
    private Integer itemid;
    private Integer id;
    private String name;
    private double price;
    private int quantity;

    public Cart() {
    }

    public Cart(Integer userid, Integer itemid, Integer id, String name, double price, int quantity) {

        this.userid = userid;
        this.itemid = itemid;
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getItemid() {
        return itemid;
    }

    public void setItemid(Integer itemid) {
        this.itemid = itemid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
