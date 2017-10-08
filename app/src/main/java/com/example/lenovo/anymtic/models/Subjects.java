package com.example.lenovo.anymtic.models;

/**
 * Created by Lenovo on 04.09.2017.
 */

public class Subjects {
     private String name, id, photoUrl, storeName;
    private int price, inOrder, imageResourceId;

    public Subjects (String name, int price, String Url, String id, int inOrder, String storeName){
        this.name = name;
        this.price = price;
        this.photoUrl = Url;
        this.id = id;
        this.inOrder = inOrder;
        this.storeName = storeName;
    }


    public int getPrice() { return price;   }
    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return photoUrl;
    }
    public void setUrl(String url) {
        this.photoUrl = url;
    }

    public String getStore() {
        return storeName;
    }
    public void setStore(String storeName) {
        this.storeName = storeName;
    }

    public int getInOrder() {        return inOrder;    }
    public void setInOrder(int inOrder) {
        this.inOrder = inOrder;
    }



    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

}