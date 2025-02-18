package com.darewro.rider.view.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.darewro.rider.data.api.models.OrderDetail;

import java.util.Comparator;
import java.util.List;

public class NearestOrder implements Parcelable {

    public static final Creator<NearestOrder> CREATOR = new Creator<NearestOrder>() {
        @Override
        public NearestOrder createFromParcel(Parcel in) {
            return new NearestOrder(in);
        }

        @Override
        public NearestOrder[] newArray(int size) {
            return new NearestOrder[size];
        }
    };

    String latitude;
    String longitude;
    String dateTimePlacement;

    protected NearestOrder(Parcel in) {
        latitude = in.readString();
        longitude = in.readString();
        dateTimePlacement = in.readString();
    }

    public NearestOrder(String latitude, String longitude, String dateTimePlacement) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateTimePlacement = dateTimePlacement;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(dateTimePlacement);

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

    public String getDateTimePlacement() {
        return dateTimePlacement;
    }

    public void setDateTimePlacement(String dateTimePlacement) {
        this.dateTimePlacement = dateTimePlacement;
    }
}
