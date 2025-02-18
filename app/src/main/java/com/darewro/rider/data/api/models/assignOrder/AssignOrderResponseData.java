package com.darewro.rider.data.api.models.assignOrder;

public class AssignOrderResponseData {
    private Integer currentAcceptedOrderCount;
    private Integer rejectedOrdersCount;
    private Integer remainingRejectionLimitCount;
    private Double deduction;
    private Double riderExtraBonus;
    private Integer currentAcceptedOrders;
    public Integer getCurrentAcceptedOrderCount() {
        return currentAcceptedOrderCount;
    }
    public void setCurrentAcceptedOrderCount(Integer currentAcceptedOrderCount) {
        this.currentAcceptedOrderCount = currentAcceptedOrderCount;
    }
    public Integer getRejectedOrdersCount() {
        return rejectedOrdersCount;
    }
    public void setRejectedOrdersCount(Integer rejectedOrdersCount) {
        this.rejectedOrdersCount = rejectedOrdersCount;
    }
    public Integer getRemainingRejectionLimitCount() {
        return remainingRejectionLimitCount;
    }
    public void setRemainingRejectionLimitCount(Integer remainingRejectionLimitCount) {
        this.remainingRejectionLimitCount = remainingRejectionLimitCount;
    }
    public Double getDeduction() {
        return deduction;
    }
    public void setDeduction(Double deduction) {
        this.deduction = deduction;
    }
    public Double getRiderExtraBonus() {
        return riderExtraBonus;
    }
    public void setRiderExtraBonus(Double riderExtraBonus) {
        this.riderExtraBonus = riderExtraBonus;
    }
    public Integer getCurrentAcceptedOrders() {
        return currentAcceptedOrders;
    }
    public void setCurrentAcceptedOrders(Integer currentAcceptedOrders) {
        this.currentAcceptedOrders = currentAcceptedOrders;
    }
}
