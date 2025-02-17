package com.darewro.riderApp.data.api.models.allOrders;

import java.util.List;

public class AllOrder {
    private int id;
    private String referenceNumber;
    private String status;
    private String orderType;
    private String created;
    private int takenUpById;
    private Integer cancelledById;
    private Integer riderAssignedById;
    private int customerID;
    private int riderID;
    private String riderName;
    private List<Integer> partnerIDs;
    private String minPreprationTime;
    private int pinId;
    private String pinColor;
    private List<Partner> partners;
    private boolean isCorporateCustomers;
    private boolean isTwoWayOrder;
    private boolean isSurpriseOrder;
    private boolean isFrequentCustomer;
    private boolean isDuplicateOrder;
    private boolean isOnlinePayment;
    private boolean isReserved;
    private boolean isMoreThan3000;
    private boolean isEdited;
    private boolean isFragile;
    private boolean isMultiplePickup;
    private boolean isMultipleDelivery;
    private int googleMapEstimatedTimeMins;
    private int googleMapEstimatedDistanceKms;
    private Customer customer;
    private List<OrderComponent> orderComponents;

    public AllOrder(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getTakenUpById() {
        return takenUpById;
    }

    public void setTakenUpById(int takenUpById) {
        this.takenUpById = takenUpById;
    }

    public Integer getCancelledById() {
        return cancelledById;
    }

    public void setCancelledById(Integer cancelledById) {
        this.cancelledById = cancelledById;
    }

    public Integer getRiderAssignedById() {
        return riderAssignedById;
    }

    public void setRiderAssignedById(Integer riderAssignedById) {
        this.riderAssignedById = riderAssignedById;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getRiderID() {
        return riderID;
    }

    public void setRiderID(int riderID) {
        this.riderID = riderID;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public List<Integer> getPartnerIDs() {
        return partnerIDs;
    }

    public void setPartnerIDs(List<Integer> partnerIDs) {
        this.partnerIDs = partnerIDs;
    }

    public String getMinPreprationTime() {
        return minPreprationTime;
    }

    public void setMinPreprationTime(String minPreprationTime) {
        this.minPreprationTime = minPreprationTime;
    }

    public int getPinId() {
        return pinId;
    }

    public void setPinId(int pinId) {
        this.pinId = pinId;
    }

    public String getPinColor() {
        return pinColor;
    }

    public void setPinColor(String pinColor) {
        this.pinColor = pinColor;
    }

    public List<Partner> getPartners() {
        return partners;
    }

    public void setPartners(List<Partner> partners) {
        this.partners = partners;
    }

    public boolean isCorporateCustomers() {
        return isCorporateCustomers;
    }

    public void setCorporateCustomers(boolean corporateCustomers) {
        isCorporateCustomers = corporateCustomers;
    }

    public boolean isTwoWayOrder() {
        return isTwoWayOrder;
    }

    public void setTwoWayOrder(boolean twoWayOrder) {
        isTwoWayOrder = twoWayOrder;
    }

    public boolean isSurpriseOrder() {
        return isSurpriseOrder;
    }

    public void setSurpriseOrder(boolean surpriseOrder) {
        isSurpriseOrder = surpriseOrder;
    }

    public boolean isFrequentCustomer() {
        return isFrequentCustomer;
    }

    public void setFrequentCustomer(boolean frequentCustomer) {
        isFrequentCustomer = frequentCustomer;
    }

    public boolean isDuplicateOrder() {
        return isDuplicateOrder;
    }

    public void setDuplicateOrder(boolean duplicateOrder) {
        isDuplicateOrder = duplicateOrder;
    }

    public boolean isOnlinePayment() {
        return isOnlinePayment;
    }

    public void setOnlinePayment(boolean onlinePayment) {
        isOnlinePayment = onlinePayment;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public boolean isMoreThan3000() {
        return isMoreThan3000;
    }

    public void setMoreThan3000(boolean moreThan3000) {
        isMoreThan3000 = moreThan3000;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public boolean isFragile() {
        return isFragile;
    }

    public void setFragile(boolean fragile) {
        isFragile = fragile;
    }

    public boolean isMultiplePickup() {
        return isMultiplePickup;
    }

    public void setMultiplePickup(boolean multiplePickup) {
        isMultiplePickup = multiplePickup;
    }

    public boolean isMultipleDelivery() {
        return isMultipleDelivery;
    }

    public void setMultipleDelivery(boolean multipleDelivery) {
        isMultipleDelivery = multipleDelivery;
    }

    public int getGoogleMapEstimatedTimeMins() {
        return googleMapEstimatedTimeMins;
    }

    public void setGoogleMapEstimatedTimeMins(int googleMapEstimatedTimeMins) {
        this.googleMapEstimatedTimeMins = googleMapEstimatedTimeMins;
    }

    public int getGoogleMapEstimatedDistanceKms() {
        return googleMapEstimatedDistanceKms;
    }

    public void setGoogleMapEstimatedDistanceKms(int googleMapEstimatedDistanceKms) {
        this.googleMapEstimatedDistanceKms = googleMapEstimatedDistanceKms;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderComponent> getOrderComponents() {
        return orderComponents;
    }

    public void setOrderComponents(List<OrderComponent> orderComponents) {
        this.orderComponents = orderComponents;
    }
}