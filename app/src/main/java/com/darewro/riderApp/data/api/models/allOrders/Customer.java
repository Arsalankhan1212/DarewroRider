package com.darewro.riderApp.data.api.models.allOrders;

public class Customer {
    private int isPartner;
    private int id;
    private String userName;
    private String email;
    private String msisdn;
    private String picturePath;
    private int rating;
    private String orderRelatedComment;
    private String orderRelatedCommentDateTime;
    private Boolean isCorporateCustomers;
    private String corporateCustomerDetails;
    private Double corporateCustomerDiscount;

    public Customer(){

    }

    public int getIsPartner() {
        return isPartner;
    }

    public void setIsPartner(int isPartner) {
        this.isPartner = isPartner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getOrderRelatedComment() {
        return orderRelatedComment;
    }

    public void setOrderRelatedComment(String orderRelatedComment) {
        this.orderRelatedComment = orderRelatedComment;
    }

    public String getOrderRelatedCommentDateTime() {
        return orderRelatedCommentDateTime;
    }

    public void setOrderRelatedCommentDateTime(String orderRelatedCommentDateTime) {
        this.orderRelatedCommentDateTime = orderRelatedCommentDateTime;
    }

    public Boolean getCorporateCustomers() {
        return isCorporateCustomers;
    }

    public void setCorporateCustomers(Boolean corporateCustomers) {
        isCorporateCustomers = corporateCustomers;
    }

    public String getCorporateCustomerDetails() {
        return corporateCustomerDetails;
    }

    public void setCorporateCustomerDetails(String corporateCustomerDetails) {
        this.corporateCustomerDetails = corporateCustomerDetails;
    }

    public Double getCorporateCustomerDiscount() {
        return corporateCustomerDiscount;
    }

    public void setCorporateCustomerDiscount(Double corporateCustomerDiscount) {
        this.corporateCustomerDiscount = corporateCustomerDiscount;
    }
}
