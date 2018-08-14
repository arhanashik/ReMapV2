package com.bodytel.remapv2.data.local.listdata;

public class ListDataModel {
    int id;
    String data;
    String date;

    public ListDataModel() {
    }

    public ListDataModel(int id, String data, String date) {
        this.id = id;
        this.data = data;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
