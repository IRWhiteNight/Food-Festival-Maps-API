package com.example.aegishub2;

public class Place {
    private String id;
    private String name;
    private String address;
    private String imgUrl;
    private String open;
    private String longitudeLatitude; // New field for longitude & latitude
    private String menu; // New field for menu

    // Constructors
    public Place() {
        // Default constructor
    }

    public Place(String id, String name, String address, String imgUrl, String open, String longitudeLatitude, String menu) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.imgUrl = imgUrl;
        this.open = open;
        this.longitudeLatitude = longitudeLatitude;
        this.menu = menu;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getLongitudeLatitude() {
        return longitudeLatitude;
    }

    public void setLongitudeLatitude(String longitudeLatitude) {
        this.longitudeLatitude = longitudeLatitude;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }
}
