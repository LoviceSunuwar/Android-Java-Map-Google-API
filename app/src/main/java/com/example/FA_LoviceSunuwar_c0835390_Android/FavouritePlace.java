package com.example.FA_LoviceSunuwar_c0835390_Android;

public class FavouritePlace {

    int id;
    String placeName;
    String address;
    String date;
    Double latitude;
    Double longitude;
    int isVisited;


    public FavouritePlace(int id, String placeName, String address, String date, Double latitude, Double longitude, int isVisited) {
        this.id = id;
        this.placeName = placeName;
        this.address = address;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isVisited = isVisited;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getIsVisited() {
        return isVisited;
    }

    public void setIsVisited(int isVisited) {
        this.isVisited = isVisited;
    }
}