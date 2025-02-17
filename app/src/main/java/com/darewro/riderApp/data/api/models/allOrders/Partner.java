package com.darewro.riderApp.data.api.models.allOrders;

import java.util.HashMap;
import java.util.Map;
public class Partner {
    private Integer id;
    private String name;
    private String contactNumber1;
    private String contactNumber2;
    private String address;
    private Integer status;
    private String logoPath;
    private Boolean featured;
    private Double minOrder;
    private Double deliveryFee;
    private Integer displayOrder;
    private String tags;
    private Object ratingCount;
    private Object rating;
    private Integer partnerType;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getContactNumber1() {
        return contactNumber1;
    }
    public void setContactNumber1(String contactNumber1) {
        this.contactNumber1 = contactNumber1;
    }
    public String getContactNumber2() {
        return contactNumber2;
    }
    public void setContactNumber2(String contactNumber2) {
        this.contactNumber2 = contactNumber2;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getLogoPath() {
        return logoPath;
    }
    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
    public Boolean getFeatured() {
        return featured;
    }
    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }
    public Double getMinOrder() {
        return minOrder;
    }
    public void setMinOrder(Double minOrder) {
        this.minOrder = minOrder;
    }
    public Double getDeliveryFee() {
        return deliveryFee;
    }
    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
    public Object getRatingCount() {
        return ratingCount;
    }
    public void setRatingCount(Object ratingCount) {
        this.ratingCount = ratingCount;
    }
    public Object getRating() {
        return rating;
    }
    public void setRating(Object rating) {
        this.rating = rating;
    }
    public Integer getPartnerType() {
        return partnerType;
    }
    public void setPartnerType(Integer partnerType) {
        this.partnerType = partnerType;
    }
}
