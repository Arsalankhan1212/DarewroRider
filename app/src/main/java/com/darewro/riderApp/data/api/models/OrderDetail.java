package com.darewro.riderApp.data.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class OrderDetail implements Parcelable {

    public static final Creator<OrderDetail> CREATOR = new Creator<OrderDetail>() {
        @Override
        public OrderDetail createFromParcel(Parcel in) {
            return new OrderDetail(in);
        }

        @Override
        public OrderDetail[] newArray(int size) {
            return new OrderDetail[size];
        }
    };
    String id;
    String isAccepted;
    String referenceNumber;
    String specialInstruction;
    String estimatedDeliveryFee;
    String estimatedDeliveryTime;
    String orderAdditionalComment;
    String dateTimePlacement;
    String source;
    String latitude;
    String longitude;
    String status;
    String estimatedOrderPrice;
    String customerID;
    String takenUpByID;
    String createdByName;
    String createdByID;
    String userID;
    String discount;
    String subAmount;
    String tax;
    String total;
    String orderType;
    List<OrderComponent> orderComponents;
    OrderLocation paymentLocation;
    Customer customer;
    Rider rider;
    boolean isCorporateCustomers;
    boolean isTwoWayOrder;
    boolean isFrequentCustomer;
    boolean isDuplicateOrder;
    boolean isOnlinePayment;
    boolean isReserved;
    boolean isMoreThan3000;
    boolean isEdited;
    boolean isFragile;
    boolean isMultiplePickup;
    boolean isMultipleDelivery;
    boolean isSurpriseOrder;
    String acceptanceDateTime;

    protected OrderDetail(Parcel in) {
        id = in.readString();
        isAccepted = in.readString();
        referenceNumber = in.readString();
        specialInstruction = in.readString();
        estimatedDeliveryFee = in.readString();
        estimatedDeliveryTime = in.readString();
        orderAdditionalComment = in.readString();
        dateTimePlacement = in.readString();
        source = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        status = in.readString();
        estimatedOrderPrice = in.readString();
        customerID = in.readString();
        takenUpByID = in.readString();
        userID = in.readString();
        discount = in.readString();
        subAmount = in.readString();
        tax = in.readString();
        total = in.readString();
        orderType = in.readString();
        orderComponents = in.createTypedArrayList(OrderComponent.CREATOR);
        paymentLocation = in.readParcelable(OrderLocation.class.getClassLoader());
        customer = in.readParcelable(Customer.class.getClassLoader());
        rider = in.readParcelable(Rider.class.getClassLoader());
        isCorporateCustomers = in.readByte() != 0;
        isTwoWayOrder = in.readByte() != 0;
        isFrequentCustomer = in.readByte() != 0;
        isDuplicateOrder = in.readByte() != 0;
        isOnlinePayment = in.readByte() != 0;
        isReserved = in.readByte() != 0;
        isMoreThan3000 = in.readByte() != 0;
        isEdited = in.readByte() != 0;
        isFragile = in.readByte() != 0;
        isMultiplePickup = in.readByte() != 0;
        isMultipleDelivery = in.readByte() != 0;
        isSurpriseOrder = in.readByte() != 0;
        createdByID = in.readString();
        createdByName = in.readString();
        acceptanceDateTime = in.readString();
    }

    public OrderDetail(String id, String isAccepted, String referenceNumber, String specialInstruction, String estimatedDeliveryFee, String estimatedDeliveryTime, String orderAdditionalComment, String dateTimePlacement, String source, String latitude, String longitude, String status, String estimatedOrderPrice, String customerID, String takenUpByID, String userID, String discount, String subAmount, String tax, String total, String orderType, List<OrderComponent> orderComponents, OrderLocation paymentLocation, Customer customer, Rider riderisCorporateCustomers, boolean isTwoWayOrder,
     boolean isFrequentCustomer,
                       boolean isDuplicateOrder,
                       boolean isOnlinePayment,
                       boolean isReserved,
                       boolean isMoreThan3000,
                       boolean isEdited ,
                       boolean isFragile,
                       boolean isMultiplePickup,
                       boolean isMultipleDelivery,
                       boolean isSurpriseOrder,
                       String createdByID,
                       String createdByName,
                       String acceptanceDateTime
    ) {
        this.id = id;
        this.isAccepted = isAccepted;
        this.referenceNumber = referenceNumber;
        this.specialInstruction = specialInstruction;
        this.estimatedDeliveryFee = estimatedDeliveryFee;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.orderAdditionalComment = orderAdditionalComment;
        this.dateTimePlacement = dateTimePlacement;
        this.source = source;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.estimatedOrderPrice = estimatedOrderPrice;
        this.customerID = customerID;
        this.takenUpByID = takenUpByID;
        this.userID = userID;
        this.discount = discount;
        this.subAmount = subAmount;
        this.tax = tax;
        this.total = total;
        this.orderType = orderType;
        this.orderComponents = orderComponents;
        this.paymentLocation = paymentLocation;
        this.customer = customer;
        this.rider = rider;
        this.isCorporateCustomers = isFrequentCustomer;
        this.isTwoWayOrder = isTwoWayOrder;
        this.isFrequentCustomer = isFrequentCustomer;
        this.isDuplicateOrder = isDuplicateOrder;
        this.isOnlinePayment = isOnlinePayment;
        this.isReserved = isReserved;
        this.isMoreThan3000 = isMoreThan3000;
        this.isEdited = isEdited;
        this.isFragile = isFragile;
        this.isMultiplePickup = isMultiplePickup;
        this.isMultipleDelivery = isMultipleDelivery;
        this.isSurpriseOrder = isSurpriseOrder;
        this.createdByID = createdByID;
        this.createdByName = createdByName;
        this.acceptanceDateTime = acceptanceDateTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(isAccepted);
        dest.writeString(referenceNumber);
        dest.writeString(specialInstruction);
        dest.writeString(estimatedDeliveryFee);
        dest.writeString(estimatedDeliveryTime);
        dest.writeString(orderAdditionalComment);
        dest.writeString(dateTimePlacement);
        dest.writeString(source);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(status);
        dest.writeString(estimatedOrderPrice);
        dest.writeString(customerID);
        dest.writeString(takenUpByID);
        dest.writeString(userID);
        dest.writeString(discount);
        dest.writeString(subAmount);
        dest.writeString(tax);
        dest.writeString(total);
        dest.writeString(orderType);
        dest.writeTypedList(orderComponents);
        dest.writeParcelable(paymentLocation, flags);
        dest.writeParcelable(customer, flags);
        dest.writeParcelable(rider, flags);
        dest.writeByte((byte)(isCorporateCustomers?1:0));
        dest.writeByte((byte)(isTwoWayOrder?1:0));
        dest.writeByte((byte)(isFrequentCustomer?1:0));
        dest.writeByte((byte)(isDuplicateOrder?1:0));
        dest.writeByte((byte)(isOnlinePayment?1:0));
        dest.writeByte((byte)(isReserved?1:0));
        dest.writeByte((byte)(isMoreThan3000?1:0));
        dest.writeByte((byte)(isEdited?1:0));
        dest.writeByte((byte)(isFragile?1:0));
        dest.writeByte((byte)(isMultiplePickup?1:0));
        dest.writeByte((byte)(isMultipleDelivery?1:0));
        dest.writeByte((byte)(isSurpriseOrder?1:0));
        dest.writeString(createdByID);
        dest.writeString(createdByName);
        dest.writeString(acceptanceDateTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getEstimatedOrderPrice() {
        return estimatedOrderPrice;
    }

    public void setEstimatedOrderPrice(String estimatedOrderPrice) {this.estimatedOrderPrice = estimatedOrderPrice;}

    public String getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(String isAccepted) {
        this.isAccepted = isAccepted;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {this.referenceNumber = referenceNumber;}

    public String getSpecialInstruction() {
        return specialInstruction;
    }

    public void setSpecialInstruction(String specialInstruction) {this.specialInstruction = specialInstruction;}

    public String getEstimatedDeliveryFee() {
        return estimatedDeliveryFee;
    }

    public void setEstimatedDeliveryFee(String estimatedDeliveryFee) {this.estimatedDeliveryFee = estimatedDeliveryFee;}

    public String getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(String estimatedDeliveryTime) {this.estimatedDeliveryTime = estimatedDeliveryTime;}

    public String getDateTimePlacement() {
        return dateTimePlacement;
    }

    public void setDateTimePlacement(String dateTimePlacement) {this.dateTimePlacement = dateTimePlacement;}

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getTakenUpByID() {
        return takenUpByID;
    }

    public void setTakenUpByID(String takenUpByID) {
        this.takenUpByID = takenUpByID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public List<OrderComponent> getOrderComponents() {
        return orderComponents;
    }

    public void setOrderComponents(List<OrderComponent> orderComponents) {
        this.orderComponents = orderComponents;
    }

    public OrderLocation getPaymentLocation() {
        return paymentLocation;
    }

    public void setPaymentLocation(OrderLocation paymentLocation) {
        this.paymentLocation = paymentLocation;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public String getOrderAdditionalComment() {
        return orderAdditionalComment;
    }

    public void setOrderAdditionalComment(String orderAdditionalComment) {
        this.orderAdditionalComment = orderAdditionalComment;
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

    public boolean isSurpriseOrder() {
        return isSurpriseOrder;
    }

    public void setSurpriseOrder(boolean surpriseOrder) {
        isSurpriseOrder = surpriseOrder;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getCreatedByID() {
        return createdByID;
    }

    public void setCreatedByID(String createdByID) {
        this.createdByID = createdByID;
    }

    public String getAcceptanceDateTime() {
        return acceptanceDateTime;
    }

    public void setAcceptanceDateTime(String acceptanceDateTime) {
        this.acceptanceDateTime = acceptanceDateTime;
    }
}
