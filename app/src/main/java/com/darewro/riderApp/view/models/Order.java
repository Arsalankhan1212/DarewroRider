package com.darewro.riderApp.view.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.darewro.riderApp.data.api.models.Customer;
import com.darewro.riderApp.data.api.models.OrderComponent;
import com.darewro.riderApp.data.api.models.OrderDetail;
import com.darewro.riderApp.data.api.models.OrderLocation;
import com.darewro.riderApp.data.api.models.Rider;

import java.util.Comparator;
import java.util.List;

public class Order implements Parcelable {

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
    public static final String NEW = "1";
    public static final String PLACED = "2";
    public static final String RIDER_ASSIGNED = "3";
    public static final String RIDER_ACCEPTED = "4";
    public static final String DELIVERED = "5";
    public static final String PAYMENT_RECEIVED = "6";
    public static final String CANCELLED = "7";

    public static final String NEW_TEXT = "New";
    public static final String PLACED_TEXT = "Placed";
    public static final String RIDER_ASSIGNED_TEXT = "Pending";
    public static final String RIDER_ACCEPTED_TEXT = "In Process";
    public static final String DELIVERED_TEXT = "Delivered";
    public static final String PAYMENT_RECEIVED_TEXT = "Payment Received";
    public static final String CANCELLED_TEXT = "Cancelled";

    public static final String COMPLETED_TEXT = "Completed";
    public static final String NEAREST_ORDERS = "Nearest";
    public static final String All_ORDERS = "All Orders";


    public static final String ORDER_TYPE_PARTNER = "1";
    public static final String ORDER_TYPE_GENERAL = "2";
    public static final String ORDER_TYPE_GENERAL_TEXT = "General";

    String date;
    String time;
    String status;
    String statusType;
    List<OrderPartners> orderPartners;
    OrderDetail orderDetail;



    public Order() {

    }

    protected Order(Parcel in) {
        date = in.readString();
        time = in.readString();
        status = in.readString();
        statusType = in.readString();
        orderPartners = in.createTypedArrayList(OrderPartners.CREATOR);
        orderDetail = in.readParcelable(OrderDetail.class.getClassLoader());
    }

    public Order(String date, String time, String status, String statusType, List<OrderPartners> orderPartners, OrderDetail orderDetail) {
        this.date = date;
        this.time = time;
        this.status = status;
        this.statusType = statusType;
        this.orderPartners = orderPartners;
        this.orderDetail = orderDetail;
    }

    public OrderDetail getOrderDetails() {
        return orderDetail;
    }

    public void setOrderDetails(OrderDetail orderDetail) {

        this.orderDetail = orderDetail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
            case PAYMENT_RECEIVED:
                status = PAYMENT_RECEIVED_TEXT;
                break;
            case CANCELLED:
                status = CANCELLED_TEXT;
                break;
        }
        this.statusType = statusType;
    }

    public List<OrderPartners> getOrderPartners() {
        return orderPartners;
    }

    public void setOrderPartners(List<OrderPartners> orderPartners) {
        this.orderPartners = orderPartners;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(status);
        parcel.writeString(statusType);
        parcel.writeTypedList(orderPartners);
        parcel.writeParcelable(orderDetail, i);
    }


    public static Comparator<Order> sortByStatus = new Comparator<Order>() {

        public int compare(Order o1, Order o2) {

            int o1Status = Integer.parseInt(o1.getOrderDetails().getStatus());
            int o2Status = Integer.parseInt(o2.getOrderDetails().getStatus());

            /*For ascending order*/
            return o1Status-o2Status;

            /*For descending order*/
            //o2Status-o1Status;
        }};


}
