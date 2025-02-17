package com.darewro.riderApp.data.api.models;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationContact implements Parcelable {

    String name;
    String contactNumber;


    protected LocationContact(Parcel in) {
        name = in.readString();
        contactNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(contactNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocationContact> CREATOR = new Creator<LocationContact>() {
        @Override
        public LocationContact createFromParcel(Parcel in) {
            return new LocationContact(in);
        }

        @Override
        public LocationContact[] newArray(int size) {
            return new LocationContact[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocationContact(String name, String contactNumber) {
        this.name = name;
        this.contactNumber = contactNumber;
    }
}
