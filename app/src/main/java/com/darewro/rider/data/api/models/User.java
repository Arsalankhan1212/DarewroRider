package com.darewro.rider.data.api.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    String id;
    String userName;
    String cnic;
    String email;
    String msisdn;
    String picturePath;
    String deviceToken;

    protected User(Parcel in) {
        id = in.readString();
        userName = in.readString();
        cnic = in.readString();
        email = in.readString();
        msisdn = in.readString();
        picturePath = in.readString();
        deviceToken = in.readString();
        bikeNumber = in.readString();
        rating = in.readString();
        riderShiftSettings = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userName);
        dest.writeString(cnic);
        dest.writeString(email);
        dest.writeString(msisdn);
        dest.writeString(picturePath);
        dest.writeString(deviceToken);
        dest.writeString(bikeNumber);
        dest.writeString(rating);
        dest.writeString(riderShiftSettings);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getBikeNumber() {
        return bikeNumber;
    }

    public void setBikeNumber(String bikeNumber) {
        this.bikeNumber = bikeNumber;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    String bikeNumber;
    String rating;
    boolean isAvailable;

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getRiderShiftSettings() {
        return riderShiftSettings;
    }

    public void setRiderShiftSettings(String riderShiftSettings) {
        this.riderShiftSettings = riderShiftSettings;
    }

    String riderShiftSettings;



    public User() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }
}
