package com.darewro.riderApp.data.api.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Rider implements Parcelable {
    public static final Creator<Rider> CREATOR = new Creator<Rider>() {
        @Override
        public Rider createFromParcel(Parcel in) {
            return new Rider(in);
        }

        @Override
        public Rider[] newArray(int size) {
            return new Rider[size];
        }
    };
    String id;
    String userName;
    String email;
    String msisdn;
    String picturePaths;
    String bikeNumber;
    String rating;

    protected Rider(Parcel in) {
        id = in.readString();
        userName = in.readString();
        email = in.readString();
        msisdn = in.readString();
        picturePaths = in.readString();
        bikeNumber = in.readString();
        rating = in.readString();
    }

    public Rider(String id, String userName, String email, String msisdn, String picturePaths, String bikeNumber, String rating) {

        this.id = id;
        this.userName = userName;
        this.email = email;
        this.msisdn = msisdn;
        this.picturePaths = picturePaths;
        this.bikeNumber = bikeNumber;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPicturePaths() {
        return picturePaths;
    }

    public void setPicturePaths(String picturePaths) {
        this.picturePaths = picturePaths;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(userName);
        parcel.writeString(email);
        parcel.writeString(msisdn);
        parcel.writeString(picturePaths);
        parcel.writeString(bikeNumber);
        parcel.writeString(rating);
    }
}
