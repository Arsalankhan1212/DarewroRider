package com.darewro.rider.data.api.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Dashboard implements Parcelable {

    public static final Creator<Dashboard> CREATOR = new Creator<Dashboard>() {
        @Override
        public Dashboard createFromParcel(Parcel in) {
            return new Dashboard(in);
        }

        @Override
        public Dashboard[] newArray(int size) {
            return new Dashboard[size];
        }
    };


//    String totalDeliveryFee;
//    String onlineBonusPercentageGranted;
//    String onlineBonusAmountGranted;
//    String orderAcceptanceBonusPercentageGranted;
//    String orderAcceptanceBonusAmountGranted;
//    String orderCountBonusPercentageGranted;
//    String orderCountBonusAmountGranted;
//    String orderFeedbackPercentageGranted;
//    String orderFeedbackAmountGranted;
//    String adjustmentAllowanceAmount;
//    String orderDeliveryTimeBonusGranted;
//    String riderBonusDailyTargetedRevenueGranted;
//    String acceptedOrders;
//    String pendingOrders;
//    String onlineTime;
//    String totalBonus;

    String cnic;
    String name;
    String msisdn;
    String picturePath;
    String bikeNumber;
    String rating;
    String shiftTimings;
    String newOrders;
    String completedOrders;
    String cancelledOrders;
    String totalOrders;
    String onlineTiming;
    String todayTrips;
    String extraTripsBonus;
    String multipleOrderBonus;
    String longDistanceBonus;
    String tripBonus;
    String adjustmentAllowance;
    String acceptanceRateToday;
    String customerRatingToday;
    String totalKmTravelledToday;
    String totalEarningOfDay;
    String outstandingAmount;
    String rejectionAmountToDeduct;
    String rejectedOrdersAmountGranted;
    String orderRejectionCountSettings;

    protected Dashboard(Parcel in) {
//        totalDeliveryFee = in.readString();
//        onlineBonusPercentageGranted = in.readString();
//        onlineBonusAmountGranted = in.readString();
//        orderAcceptanceBonusPercentageGranted = in.readString();
//        orderAcceptanceBonusAmountGranted = in.readString();
//        orderCountBonusPercentageGranted = in.readString();
//        orderCountBonusAmountGranted = in.readString();
//        orderFeedbackPercentageGranted = in.readString();
//        orderFeedbackAmountGranted = in.readString();
        cnic = in.readString();
        name = in.readString();
        msisdn = in.readString();
        picturePath = in.readString();
        bikeNumber = in.readString();
        rating = in.readString();
        shiftTimings = in.readString();


        adjustmentAllowance = in.readString();
//        adjustmentAllowanceAmount = in.readString();
//        orderDeliveryTimeBonusGranted = in.readString();
//        riderBonusDailyTargetedRevenueGranted = in.readString();
        completedOrders = in.readString();
        totalOrders = in.readString();
//        acceptedOrders = in.readString();
//        pendingOrders = in.readString();
//        onlineTime = in.readString();
//        totalBonus = in.readString();
        newOrders = in.readString();
        cancelledOrders = in.readString();
        onlineTiming = in.readString();
        todayTrips = in.readString();
        extraTripsBonus = in.readString();
        multipleOrderBonus = in.readString();
        longDistanceBonus = in.readString();
        tripBonus = in.readString();
        acceptanceRateToday = in.readString();
        customerRatingToday = in.readString();
        totalKmTravelledToday = in.readString();
        totalEarningOfDay = in.readString();
        outstandingAmount = in.readString();
        rejectionAmountToDeduct = in.readString();
        rejectedOrdersAmountGranted = in.readString();
        orderRejectionCountSettings = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(totalDeliveryFee);
//        dest.writeString(onlineBonusPercentageGranted);
//        dest.writeString(onlineBonusAmountGranted);
//        dest.writeString(orderAcceptanceBonusPercentageGranted);
//        dest.writeString(orderAcceptanceBonusAmountGranted);
//        dest.writeString(orderCountBonusPercentageGranted);
//        dest.writeString(orderCountBonusAmountGranted);
//        dest.writeString(orderFeedbackPercentageGranted);
//        dest.writeString(orderFeedbackAmountGranted);
        dest.writeString(cnic);
        dest.writeString(name);
        dest.writeString(msisdn);
        dest.writeString(bikeNumber);
        dest.writeString(picturePath);
        dest.writeString(rating);
        dest.writeString(shiftTimings);


        dest.writeString(adjustmentAllowance);
//        dest.writeString(adjustmentAllowanceAmount);
//        dest.writeString(orderDeliveryTimeBonusGranted);
//        dest.writeString(riderBonusDailyTargetedRevenueGranted);
        dest.writeString(completedOrders);
        dest.writeString(totalOrders);

        dest.writeString(newOrders);
        dest.writeString(cancelledOrders);
        dest.writeString(onlineTiming);
        dest.writeString(todayTrips);
        dest.writeString(extraTripsBonus);
        dest.writeString(multipleOrderBonus);
        dest.writeString(longDistanceBonus);
        dest.writeString(tripBonus);
        dest.writeString(acceptanceRateToday);
        dest.writeString(customerRatingToday);
        dest.writeString(totalKmTravelledToday);
        dest.writeString(totalEarningOfDay);
        dest.writeString(outstandingAmount);
        dest.writeString(rejectionAmountToDeduct);
        dest.writeString(rejectedOrdersAmountGranted);
        dest.writeString(orderRejectionCountSettings);
//        dest.writeString(acceptedOrders);
//        dest.writeString(pendingOrders);
//        dest.writeString(onlineTime);
//        dest.writeString(totalBonus);

    }

    @Override
    public int describeContents() {
        return 0;
    }

//    public String getTotalDeliveryFee() {
//        return totalDeliveryFee;
//    }
//
//    public void setTotalDeliveryFee(String totalDeliveryFee) {
//        this.totalDeliveryFee = totalDeliveryFee;
//    }
//
//    public String getOnlineBonusPercentageGranted() {
//        return onlineBonusPercentageGranted;
//    }
//
//    public void setOnlineBonusPercentageGranted(String onlineBonusPercentageGranted) {
//        this.onlineBonusPercentageGranted = onlineBonusPercentageGranted;
//    }
//
//    public String getOnlineBonusAmountGranted() {
//        return onlineBonusAmountGranted;
//    }
//
//    public void setOnlineBonusAmountGranted(String onlineBonusAmountGranted) {
//        this.onlineBonusAmountGranted = onlineBonusAmountGranted;
//    }
//
//    public String getOrderAcceptanceBonusPercentageGranted() {
//        return orderAcceptanceBonusPercentageGranted;
//    }
//
//    public void setOrderAcceptanceBonusPercentageGranted(String orderAcceptanceBonusPercentageGranted) {
//        this.orderAcceptanceBonusPercentageGranted = orderAcceptanceBonusPercentageGranted;
//    }
//
//    public String getOrderAcceptanceBonusAmountGranted() {
//        return orderAcceptanceBonusAmountGranted;
//    }
//
//    public void setOrderAcceptanceBonusAmountGranted(String orderAcceptanceBonusAmountGranted) {
//        this.orderAcceptanceBonusAmountGranted = orderAcceptanceBonusAmountGranted;
//    }
//
//    public String getOrderCountBonusPercentageGranted() {
//        return orderCountBonusPercentageGranted;
//    }
//
//    public void setOrderCountBonusPercentageGranted(String orderCountBonusPercentageGranted) {
//        this.orderCountBonusPercentageGranted = orderCountBonusPercentageGranted;
//    }
//
//    public String getOrderCountBonusAmountGranted() {
//        return orderCountBonusAmountGranted;
//    }
//
//    public void setOrderCountBonusAmountGranted(String orderCountBonusAmountGranted) {
//        this.orderCountBonusAmountGranted = orderCountBonusAmountGranted;
//    }
//
//    public String getOrderFeedbackPercentageGranted() {
//        return orderFeedbackPercentageGranted;
//    }
//
//    public void setOrderFeedbackPercentageGranted(String orderFeedbackPercentageGranted) {
//        this.orderFeedbackPercentageGranted = orderFeedbackPercentageGranted;
//    }
//
//    public String getOrderFeedbackAmountGranted() {
//        return orderFeedbackAmountGranted;
//    }
//
//    public void setOrderFeedbackAmountGranted(String orderFeedbackAmountGranted) {
//        this.orderFeedbackAmountGranted = orderFeedbackAmountGranted;
//    }
//
//    public String getAdjustmentAllowance() {
//        return adjustmentAllowance;
//    }
//
//    public void setAdjustmentAllowance(String adjustmentAllowance) {
//        this.adjustmentAllowance = adjustmentAllowance;
//    }
//
//    public String getAdjustmentAllowanceAmount() {
//        return adjustmentAllowanceAmount;
//    }
//
//    public void setAdjustmentAllowanceAmount(String adjustmentAllowanceAmount) {
//        this.adjustmentAllowanceAmount = adjustmentAllowanceAmount;
//    }
//
//    public String getOrderDeliveryTimeBonusGranted() {
//        return orderDeliveryTimeBonusGranted;
//    }
//
//    public void setOrderDeliveryTimeBonusGranted(String orderDeliveryTimeBonusGranted) {
//        this.orderDeliveryTimeBonusGranted = orderDeliveryTimeBonusGranted;
//    }
//
//    public String getRiderBonusDailyTargetedRevenueGranted() {
//        return riderBonusDailyTargetedRevenueGranted;
//    }
//
//    public void setRiderBonusDailyTargetedRevenueGranted(String riderBonusDailyTargetedRevenueGranted) {
//        this.riderBonusDailyTargetedRevenueGranted = riderBonusDailyTargetedRevenueGranted;
//    }
//
//    public String getCompletedOrders() {
//        return completedOrders;
//    }
//
//    public void setCompletedOrders(String completedOrders) {
//        this.completedOrders = completedOrders;
//    }
//
//    public String getTotalOrders() {
//        return totalOrders;
//    }
//
//    public void setTotalOrders(String totalOrders) {
//        this.totalOrders = totalOrders;
//    }
//
//    public String getAcceptedOrders() {
//        return acceptedOrders;
//    }
//
//    public void setAcceptedOrders(String acceptedOrders) {
//        this.acceptedOrders = acceptedOrders;
//    }
//
//    public String getPendingOrders() {
//        return pendingOrders;
//    }
//
//    public void setPendingOrders(String pendingOrders) {
//        this.pendingOrders = pendingOrders;
//    }
//
//    public String getOnlineTime() {
//        return onlineTime;
//    }
//
//    public void setOnlineTime(String onlineTime) {
//        this.onlineTime = onlineTime;
//    }
//
//    public String getTotalBonus() {
//        return totalBonus;
//    }
//
//    public void setTotalBonus(String totalBonus) {
//        this.totalBonus = totalBonus;
//    }

    public String getNewOrders() {
        return newOrders;
    }

    public void setNewOrders(String newOrders) {
        this.newOrders = newOrders;
    }

    public String getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(String completedOrders) {
        this.completedOrders = completedOrders;
    }

    public String getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(String cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public String getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(String totalOrders) {
        this.totalOrders = totalOrders;
    }

    public String getOnlineTiming() {
        return onlineTiming;
    }

    public void setOnlineTiming(String onlineTiming) {
        this.onlineTiming = onlineTiming;
    }

    public String getTodayTrips() {
        return todayTrips;
    }

    public void setTodayTrips(String todayTrips) {
        this.todayTrips = todayTrips;
    }

    public String getExtraTripsBonus() {
        return extraTripsBonus;
    }

    public void setExtraTripsBonus(String extraTripsBonus) {
        this.extraTripsBonus = extraTripsBonus;
    }

    public String getMultipleOrderBonus() {
        return multipleOrderBonus;
    }

    public void setMultipleOrderBonus(String multipleOrderBonus) {
        this.multipleOrderBonus = multipleOrderBonus;
    }

    public String getLongDistanceBonus() {
        return longDistanceBonus;
    }

    public void setLongDistanceBonus(String longDistanceBonus) {
        this.longDistanceBonus = longDistanceBonus;
    }

    public String getTripBonus() {
        return tripBonus;
    }

    public void setTripBonus(String tripBonus) {
        this.tripBonus = tripBonus;
    }

    public String getAdjustmentAllowance() {
        return adjustmentAllowance;
    }

    public void setAdjustmentAllowance(String adjustmentAllowance) {
        this.adjustmentAllowance = adjustmentAllowance;
    }

    public String getAcceptanceRateToday() {
        return acceptanceRateToday;
    }

    public void setAcceptanceRateToday(String acceptanceRateToday) {
        this.acceptanceRateToday = acceptanceRateToday;
    }

    public String getCustomerRatingToday() {
        return customerRatingToday;
    }

    public void setCustomerRatingToday(String customerRatingToday) {
        this.customerRatingToday = customerRatingToday;
    }

    public String getTotalKmTravelledToday() {
        return totalKmTravelledToday;
    }

    public void setTotalKmTravelledToday(String totalKmExtraTotalelledToday) {
        this.totalKmTravelledToday = totalKmExtraTotalelledToday;
    }

    public String getTotalEarningOfDay() {
        return totalEarningOfDay;
    }

    public void setTotalEarningOfDay(String totalEarningOfDay) {
        this.totalEarningOfDay = totalEarningOfDay;
    }

    public String getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(String outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getBikeNumber() {
        return bikeNumber;
    }

    public void setBikeNumber(String bikeNumber) {
        this.bikeNumber = bikeNumber;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getShiftTimings() {
        return shiftTimings;
    }

    public void setShiftTimings(String shiftTimings) {
        this.shiftTimings = shiftTimings;
    }

    public String getRejectionAmountToDeduct() {
        return rejectionAmountToDeduct;
    }

    public void setRejectionAmountToDeduct(String rejectionAmountToDeduct) {
        this.rejectionAmountToDeduct = rejectionAmountToDeduct;
    }

    public String getRejectedOrdersAmountGranted() {
        return rejectedOrdersAmountGranted;
    }

    public void setRejectedOrdersAmountGranted(String rejectedOrdersAmountGranted) {
        this.rejectedOrdersAmountGranted = rejectedOrdersAmountGranted;
    }

    public String getOrderRejectionCountSettings() {
        return orderRejectionCountSettings;
//                "[{\"orderCount\": 2,\"monthlyOrderRejectionCount\": 10},{\"orderCount\": 1,\"monthlyOrderRejectionCount\": 5},{\"orderCount\": 2,\"monthlyOrderRejectionCount\": 4}]";
    }

    public void setOrderRejectionCountSettings(String orderRejectionCountSettings) {
        this.orderRejectionCountSettings = orderRejectionCountSettings;
    }
}
