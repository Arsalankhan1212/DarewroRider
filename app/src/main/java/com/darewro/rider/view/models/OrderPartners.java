package com.darewro.rider.view.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.darewro.rider.data.api.models.OrderComponent;
import com.darewro.rider.data.api.models.OrderLocation;
import com.darewro.rider.data.api.models.Packages;

import java.util.List;

public class OrderPartners implements Parcelable {

    public static final String NEW = "1";
    public static final String PENDING = "2";
    public static final String ACCEPTED = "3";
    public static final String REJECTED = "4";
    public static final String PREPARED = "5";
    public static final String PICKED = "6";
    public static final String CANCELLED = "7";

    public static final String NEW_TEXT = "New";
    public static final String PENDING_TEXT = "Pending";
    public static final String ACCEPTED_TEXT = "Accepted";
    public static final String REJECTED_TEXT = "Rejected";
    public static final String PREPARED_TEXT = "Prepared";
    public static final String PICKED_TEXT = "Picked";
    public static final String CANCELLED_TEXT = "Cancelled";

    String name;
    String price;
    String subTotal;
    String discount;
    String deliveryLocation;
    String deliveryContact;
    List<Packages> packages;
    String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    protected OrderPartners(Parcel in) {
        name = in.readString();
        price = in.readString();
        subTotal = in.readString();
        discount = in.readString();
        deliveryLocation = in.readString();
        deliveryContact = in.readString();
        status = in.readString();
        id = in.readString();
        packages = in.createTypedArrayList(Packages.CREATOR);

    }

    public OrderPartners(String name, String price, String deliveryLocation, List<Packages> packages, String status, String id, String deliveryContact, String subTotal, String discount) {
        this.name = name;
        this.price = price;
        this.deliveryLocation = deliveryLocation;
        this.deliveryContact = deliveryContact;
        this.status = status;
        this.id = id;
        this.packages = packages;
        this.subTotal = subTotal;
        this.discount = discount;
    }

    public static final Creator<OrderPartners> CREATOR = new Creator<OrderPartners>() {
        @Override
        public OrderPartners createFromParcel(Parcel in) {
            return new OrderPartners(in);
        }

        @Override
        public OrderPartners[] newArray(int size) {
            return new OrderPartners[size];
        }
    };

    public OrderPartners() {

    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(price);
        parcel.writeString(subTotal);
        parcel.writeString(discount);
        parcel.writeString(deliveryLocation);
        parcel.writeString(deliveryContact);
        parcel.writeString(status);
        parcel.writeString(id);
        parcel.writeTypedList(packages);


    }

    public void setPackages(List<Packages> packages) {
        this.packages = packages;
    }
    public List<Packages> getPackages() {
        return  this.packages;
    }

    public String getDeliveryContact() {
        return deliveryContact;
    }

    public void setDeliveryContact(String deliveryContact) {
        this.deliveryContact = deliveryContact;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
