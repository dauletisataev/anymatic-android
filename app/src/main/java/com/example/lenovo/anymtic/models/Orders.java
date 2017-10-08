package com.example.lenovo.anymtic.models;

import java.util.ArrayList;

public class Orders {
    private int totalPrice;
    private String date;
    private ArrayList<String> items;

    public Orders(String date, int totalPrice, ArrayList<String> items){
        this.totalPrice = totalPrice;
        this.items = items;
        this.date = date;
    }


    public int getPrice() { return totalPrice;   }
    public void setPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ArrayList<String> getItems() {
        return items;
    }
    public void setItems(ArrayList<String> items) {        this.items = items;    }

    public String getDate() {        return date;    }
    public void setDate(String date) {        this.date = date;    }
}