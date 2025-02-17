package com.darewro.riderApp.data.api.handlers;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.darewro.riderApp.data.api.models.OrderComponent;
import com.darewro.riderApp.data.api.models.OrderDetail;
import com.darewro.riderApp.data.api.models.OrderLocation;
import com.darewro.riderApp.presenter.BaseResponseListener;
import com.darewro.riderApp.presenter.ResponseListenerOrdersDetails;
import com.darewro.riderApp.view.models.Order;
import com.darewro.riderApp.view.models.OrderPartners;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.LocationUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class OrdersDetailsHandler extends ApiBaseResponseHandler {

    ResponseListenerOrdersDetails responseListenerordersDetails;

    public OrdersDetailsHandler(Activity activity, String calledApi, BaseResponseListener baseResponseListener) {
        super(activity, calledApi, baseResponseListener);
        this.responseListenerordersDetails = (ResponseListenerOrdersDetails) baseResponseListener;

    }


    @Override
    public void setApiResponse(JSONObject jsonObject, String calledApi) {
        BackgroundTask backgroundTask = new BackgroundTask(jsonObject.toString());
        backgroundTask.execute();
    }

    @Override
    public void setApiResponse(JSONArray jsonArray, String calledApi) {

    }

    @Override
    public void setApiResponse(String jsonAsString, String calledApi) {
        BackgroundTask backgroundTask = new BackgroundTask(jsonAsString);
        backgroundTask.execute();
    }

    @Override
    public void setApiError(String calledApi) {

    }

    @Override
    public void setApiError(String errorMessage, String calledApi) {

    }

    @Override
    public void setApiError(String errorMessage, String calledApi, int errorCode) {
        responseListenerordersDetails.onError(calledApi,errorMessage,errorCode);
    }

    class BackgroundTask extends AsyncTask<Void, Void, Order> {
        String ordersJson;
        Order order = null;
        OrderDetail orderDetail = null;

        public BackgroundTask(String jsonAsString) {
            this.ordersJson = jsonAsString;
        }

        @Override
        protected Order doInBackground(Void... voids) {
            ExclusionStrategy exclusionStrategy = new ExclusionStrategy() {

                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    return false;
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return clazz == Field.class || clazz == Method.class;
                }
            };

            Gson gson = new GsonBuilder()
                    .addSerializationExclusionStrategy(exclusionStrategy)
                    .addDeserializationExclusionStrategy(exclusionStrategy)
                    .create();

            try {
                orderDetail = gson.fromJson(String.valueOf(new JSONObject(ordersJson)), new TypeToken<OrderDetail>() {
                }.getType());

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (orderDetail != null) {
                    order = new Order();
                    order.setDate(AppUtils.getDate(orderDetail.getDateTimePlacement()));
                    order.setTime(AppUtils.getTime(orderDetail.getDateTimePlacement()));
                    order.setStatusType(orderDetail.getStatus());

                    List<OrderPartners> orderPartnerList = new ArrayList<>();

                    for (OrderComponent orderComponent : orderDetail.getOrderComponents()) {
                        OrderPartners orderPartner = new OrderPartners();
                        orderPartner.setName(orderComponent.getPartnerName());
                        orderPartner.setPackages(orderComponent.getPackages());
                        orderPartner.setPrice(orderDetail.getTotal());
                        orderPartner.setSubTotal(orderDetail.getSubAmount());
                        orderPartner.setDiscount(orderDetail.getDiscount());
                        orderPartner.setStatus(orderComponent.getStatus());
                        orderPartner.setId(orderComponent.getId());

                        OrderLocation deliveryLocation = null;
                        for (OrderLocation orderLocation : orderComponent.getOrderLocations()) {
                            if (orderLocation.getLocationType().equalsIgnoreCase(LocationUtils.TYPE_DELIVERY)) {
                                deliveryLocation = orderLocation;
                                LatLng latlng = new LatLng(Double.parseDouble(orderLocation.getLatitude()),Double.parseDouble(orderLocation.getLongitude()));
                                String locationName = LocationUtils.getAddress(activity, latlng);

                                if(locationName!=null&&(!locationName.equals("")))
                                    deliveryLocation.setName(locationName);
                                else
                                    deliveryLocation.setName(LocationUtils.TYPE_DELIVERY_STRING);

                                if(orderLocation.getLocationContacts()!=null && deliveryLocation.getLocationContacts().size()>0){
                                    deliveryLocation.setLocationContacts(orderLocation.getLocationContacts());
                                }
                            }
                        }
                        orderPartner.setDeliveryLocation(deliveryLocation.getName());
                        if(deliveryLocation.getLocationContacts()!=null && deliveryLocation.getLocationContacts().size()>0)
                            orderPartner.setDeliveryContact(deliveryLocation.getLocationContacts().get(0).getContactNumber());
                        orderPartnerList.add(orderPartner);
                    }
                    order.setOrderPartners(orderPartnerList);
                    order.setOrderDetails(orderDetail);

            }

            return order;
        }

        @Override
        protected void onPostExecute(Order aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid != null) {
                responseListenerordersDetails.onSuccess(calledApi, aVoid);
            } else if (ordersJson != null) {
                responseListenerordersDetails.onSuccess(calledApi, ordersJson);
            } else {
                responseListenerordersDetails.onError(calledApi);
            }
        }

        @Override
        protected void onCancelled(Order aVoid) {
            super.onCancelled(aVoid);
            responseListenerordersDetails.onError(calledApi);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            responseListenerordersDetails.onError(calledApi);
        }
    }

}
