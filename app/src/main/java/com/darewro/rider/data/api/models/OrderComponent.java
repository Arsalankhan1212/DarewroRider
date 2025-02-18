package com.darewro.rider.data.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class OrderComponent implements Parcelable {

    public static final Creator<OrderComponent> CREATOR = new Creator<OrderComponent>() {
        @Override
        public OrderComponent createFromParcel(Parcel in) {
            return new OrderComponent(in);
        }

        @Override
        public OrderComponent[] newArray(int size) {
            return new OrderComponent[size];
        }
    };
    String id;
    String status;
    String discount;
    String subAmount;
    String tax;
    String total;
    String orderID;
    String partnerID;
    String partnerName;
    String estimatedOrderPrice;
    List<Packages> packages;
    List<OrderLocation> orderLocations;

    protected OrderComponent(Parcel in) {
        id = in.readString();
        status = in.readString();
        discount = in.readString();
        subAmount = in.readString();
        tax = in.readString();
        total = in.readString();
        orderID = in.readString();
        partnerID = in.readString();
        partnerName = in.readString();
        estimatedOrderPrice = in.readString();
        packages = in.createTypedArrayList(Packages.CREATOR);
        orderLocations = in.createTypedArrayList(OrderLocation.CREATOR);
    }

    public OrderComponent(String id, String status, String discount, String subAmount, String tax, String total, String orderID, String partnerID, String partnerName, List<Packages> packages, List<OrderLocation> orderLocations) {
        this.id = id;
        this.status = status;
        this.discount = discount;
        this.subAmount = subAmount;
        this.tax = tax;
        this.total = total;
        this.orderID = orderID;
        this.partnerID = partnerID;
        this.partnerName = partnerName;
        this.packages = packages;
        this.orderLocations = orderLocations;
    }

    public String getEstimatedOrderPrice() {
        return estimatedOrderPrice;
    }

    public void setEstimatedOrderPrice(String estimatedOrderPrice) {
        this.estimatedOrderPrice = estimatedOrderPrice;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(status);
        dest.writeString(discount);
        dest.writeString(subAmount);
        dest.writeString(tax);
        dest.writeString(total);
        dest.writeString(orderID);
        dest.writeString(partnerID);
        dest.writeString(partnerName);
        dest.writeString(estimatedOrderPrice);
        dest.writeTypedList(packages);
        dest.writeTypedList(orderLocations);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getSubAmount() {
        return subAmount;
    }

    public void setSubAmount(String subAmount) {
        this.subAmount = subAmount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getPartnerID() {
        return partnerID;
    }

    public void setPartnerID(String partnerID) {
        this.partnerID = partnerID;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public List<Packages> getPackages() {
        return packages;
    }

    public void setPackages(List<Packages> packages) {
        this.packages = packages;
    }

    public List<OrderLocation> getOrderLocations() {

        return orderLocations;
    }

    public void setOrderLocations(List<OrderLocation> orderLocations) {
        this.orderLocations = orderLocations;
    }
}
