package com.darewro.riderApp.data.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class OrderLocation implements Parcelable {

    String id;
    String locationID;
    String locationType;
    String latitude;
    String longitude;
    String name;
    String manualLocation;
    List<LocationContact> locationContacts;

    public String getManualLocation() {
        return manualLocation;
    }

    public void setManualLocation(String manualLocation) {
        this.manualLocation = manualLocation;
    }

    protected OrderLocation(Parcel in) {
        id = in.readString();
        locationID = in.readString();
        locationType = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        name = in.readString();
        manualLocation = in.readString();
        locationContacts = in.createTypedArrayList(LocationContact.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(locationID);
        dest.writeString(locationType);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(name);
        dest.writeString(manualLocation);
        dest.writeTypedList(locationContacts);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderLocation> CREATOR = new Creator<OrderLocation>() {
        @Override
        public OrderLocation createFromParcel(Parcel in) {
            return new OrderLocation(in);
        }

        @Override
        public OrderLocation[] newArray(int size) {
            return new OrderLocation[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LocationContact> getLocationContacts() {
        return locationContacts;
    }

    public void setLocationContacts(List<LocationContact> locationContacts) {
        this.locationContacts = locationContacts;
    }

    public OrderLocation(String id, String locationID, String locationType, String latitude, String longitude, String name, String manualLocation, List<LocationContact> locationContacts) {
        this.id = id;
        this.locationID = locationID;
        this.locationType = locationType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.manualLocation = manualLocation;
        this.locationContacts = locationContacts;
    }
}
