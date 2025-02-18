package com.darewro.rider.data.db.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "OrderPath")
public class OrderPath extends Model {

    @Column(name = "timeStamp", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    long timeStamp;
    @Column(name = "orderID")
    String orderID;
    @Column(name = "lat")
    String lat;
    @Column(name = "lng")
    String lng;
    @Column(name = "isRepeated")
    boolean isRepeated;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public boolean isRepeated() {
        return isRepeated;
    }

    public void setRepeated(boolean repeated) {
        this.isRepeated = repeated;
    }
}
