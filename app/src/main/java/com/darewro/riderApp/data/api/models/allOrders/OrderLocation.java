package com.darewro.riderApp.data.api.models.allOrders;

import java.util.List;

public class OrderLocation {
    private int id;
    private Integer locationID;
    private int locationType;
    private double latitude;
    private double longitude;
    private String locationName;
    private String manualLocation;
    private String name;
    private boolean isAlive;
    private List<LocationContact> locationContacts;

    public OrderLocation(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getLocationID() {
        return locationID;
    }

    public void setLocationID(Integer locationID) {
        this.locationID = locationID;
    }

    public int getLocationType() {
        return locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getManualLocation() {
        return manualLocation;
    }

    public void setManualLocation(String manualLocation) {
        this.manualLocation = manualLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public List<LocationContact> getLocationContacts() {
        return locationContacts;
    }

    public void setLocationContacts(List<LocationContact> locationContacts) {
        this.locationContacts = locationContacts;
    }
}
