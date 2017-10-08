package com.example.lenovo.anymtic.models;

/**
 * Created by Lenovo on 04.09.2017.
 */

public class BusketItems {
     private String name, id;
    private int price, count;

    public BusketItems(String name, int price, String id, int count){
        this.name = name;
        this.price = price;
        this.id = id;
        this.count = count;
    }


    public int getPrice() { return price;   }
    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {        this.name = name;    }

    public int getCount() {        return count;    }

    public void setCount(int count) {        this.count = count;    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }



}