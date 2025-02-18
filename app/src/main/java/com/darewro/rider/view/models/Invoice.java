package com.darewro.rider.view.models;

/**
 * Created by KMajeed on 15/01/2019.
 */

import android.os.Parcel;
import android.os.Parcelable;


import com.darewro.rider.data.api.models.OrderDetail;

import java.util.List;

public class Invoice implements Parcelable {

    public static final String NEW = "1";
    public static final String PLACED = "2";
    public static final String RIDER_ASSIGNED = "3";
    public static final String RIDER_ACCEPTED = "4";
    public static final String DELIVERED = "5";
    public static final String CANCELLED = "6";
    public static final String NEW_TEXT = "New";
    public static final String PLACED_TEXT = "Placed";
    public static final String RIDER_ASSIGNED_TEXT = "Rider Assigned";
    public static final String RIDER_ACCEPTED_TEXT = "In Process";
    public static final String DELIVERED_TEXT = "Delivered";
    public static final String CANCELLED_TEXT = "Cancelled";
    public static final Creator<Invoice> CREATOR = new Creator<Invoice>() {
        @Override
        public Invoice createFromParcel(Parcel in) {
            return new Invoice(in);
        }

        @Override
        public Invoice[] newArray(int size) {
            return new Invoice[size];
        }
    };

    String id;
    String orderId;
    String status;
    String statusType;
    String discount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getSubamount() {
        return subAmount;
    }

    public void setSubamount(String subamount) {
        this.subAmount = subamount;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<InvoicePartner> getInvoicePartners() {
        return invoicePartners;
    }

    public void setInvoicePartners(List<InvoicePartner> invoicePartners) {
        this.invoicePartners = invoicePartners;
    }

    String subAmount;
    String tax;
    String total;
    String userId;

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        deliveryCharges = deliveryCharges;
    }
    String deliveryCharges;
    List<InvoicePartner> invoicePartners;

    public Invoice() {

    }

    protected Invoice(Parcel in) {
        id = in.readString();
        orderId = in.readString();
        status = in.readString();
        statusType = in.readString();
        discount = in.readString();
        subAmount = in.readString();
        tax = in.readString();
        total = in.readString();
        userId = in.readString();
        deliveryCharges = in.readString();
        invoicePartners = in.createTypedArrayList(InvoicePartner.CREATOR);
    }


    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        switch (statusType) {
            case NEW:
                status = NEW_TEXT;
                break;
            case PLACED:
                status = PLACED_TEXT;
                break;
            case RIDER_ASSIGNED:
                status = RIDER_ASSIGNED_TEXT;
                break;
            case RIDER_ACCEPTED:
                status = RIDER_ACCEPTED_TEXT;
                break;
            case DELIVERED:
                status = DELIVERED_TEXT;
                break;
            case CANCELLED:
                status = CANCELLED_TEXT;
                break;
        }
        this.statusType = statusType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(id);
        parcel.writeString(orderId);
        parcel.writeString(status);
        parcel.writeString(statusType);
        parcel.writeString(discount);
        parcel.writeString(subAmount);
        parcel.writeString(tax);
        parcel.writeString(total);
        parcel.writeString(userId);
        parcel.writeString(deliveryCharges);
        parcel.writeTypedList(invoicePartners);
    }
}

