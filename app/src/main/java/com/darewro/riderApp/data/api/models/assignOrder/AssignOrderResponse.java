package com.darewro.riderApp.data.api.models.assignOrder;

import com.google.gson.annotations.SerializedName;

public class AssignOrderResponse {
    @SerializedName("returnCode") // Maps the JSON field
    private int returnCode;

    @SerializedName("message")
    private String message;

    @SerializedName("response")
    private AssignOrderResponseData response;

    public Integer getReturnCode() {
        return returnCode;
    }
    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public AssignOrderResponseData getResponse() {
        return response;
    }
    public void setResponse(AssignOrderResponseData response) {
        this.response = response;
    }
}
