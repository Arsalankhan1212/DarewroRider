package com.darewro.riderApp.data.api.models.allOrders;

import java.util.List;

public class AllOrderResponse {
    private int returnCode;
    private String message;
    private List<AllOrder> response;

    public AllOrderResponse(){

    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AllOrder> getResponse() {
        return response;
    }

    public void setResponse(List<AllOrder> response) {
        this.response = response;
    }
}
