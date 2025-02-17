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
import com.darewro.riderApp.data.db.model.OrdersTable;
import com.darewro.riderApp.presenter.BaseResponseListener;
import com.darewro.riderApp.presenter.ResponseListenerOrdersListing;
import com.darewro.riderApp.view.models.Order;
import com.darewro.riderApp.view.models.OrderPartners;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.LocationUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class OrdersListingHandler extends ApiBaseResponseHandler {

    ResponseListenerOrdersListing responseListenerOrdersListing;

    public OrdersListingHandler(Activity activity, String calledApi, BaseResponseListener baseResponseListener) {
        super(activity, calledApi, baseResponseListener);
        this.responseListenerOrdersListing = (ResponseListenerOrdersListing) baseResponseListener;

    }


    @Override
    public void setApiResponse(JSONObject jsonObject, String calledApi) {
        BackgroundTask backgroundTask = new BackgroundTask(jsonObject.toString());
        backgroundTask.execute();
    }

    @Override
    public void setApiResponse(JSONArray jsonArray, String calledApi) {
//        BackgroundTask backgroundTask = new BackgroundTask(jsonArray.toString());
//        backgroundTask.execute();
    }

    @Override
    public void setApiResponse(String jsonAsString, String calledApi) {


    }

    @Override
    public void setApiError(String calledApi) {
        responseListenerOrdersListing.onError(calledApi);

    }

    @Override
    public void setApiError(String errorMessage, String calledApi) {
        responseListenerOrdersListing.onError(calledApi, errorMessage);

    }

    @Override
    public void setApiError(String errorMessage, String calledApi, int errorCode) {
        responseListenerOrdersListing.onError(calledApi, errorMessage,errorCode);

    }

    class BackgroundTask extends AsyncTask<Void, Void, List<Order>> {
        String ordersJson;
        List<OrderDetail> orderDetails = null;
        List<Order> orders = new ArrayList<>();
        List<OrderDetail> completedOrderDetails = null;
        List<Order> completedOrders = new ArrayList<>();

        String id = "";
        boolean hasPending = false;

        public BackgroundTask(String jsonAsString) {
            this.ordersJson = jsonAsString;
        }

        @Override
        protected List<Order> doInBackground(Void... voids) {

//            deleteOrders();
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
                orderDetails = gson.fromJson(String.valueOf(new JSONObject(ordersJson).optJSONArray("newOrders")), new TypeToken<List<OrderDetail>>() {
                }.getType());

                completedOrderDetails = gson.fromJson(String.valueOf(new JSONObject(ordersJson).optJSONArray("completedOrders")), new TypeToken<List<OrderDetail>>() {
                }.getType());


            } catch (Exception e) {
                e.printStackTrace();
            }

            if (orderDetails != null) {
                for (OrderDetail orderDetail : orderDetails) {

                    try {
                        Order order = new Order();
                        order.setDate(AppUtils.getDate(orderDetail.getDateTimePlacement()));
                        order.setTime(AppUtils.getTime(orderDetail.getDateTimePlacement()));
                        order.setStatusType(orderDetail.getStatus());

                        List<OrderPartners> orderPartnerList = new ArrayList<>();

                        for (OrderComponent orderComponent : orderDetail.getOrderComponents()) {
                            OrderPartners orderPartner = new OrderPartners();
                            orderPartner.setName(orderComponent.getPartnerName());
                            orderPartner.setPackages(orderComponent.getPackages());
                            orderPartner.setPrice(orderDetail.getTotal());
                            orderPartner.setDiscount(orderDetail.getDiscount());
                            orderPartner.setSubTotal(orderDetail.getSubAmount());
                            orderPartner.setStatus(orderComponent.getStatus());
                            orderPartner.setId(orderComponent.getId());

                            OrderLocation deliveryLocation = null;
                            for (OrderLocation orderLocation : orderComponent.getOrderLocations()) {
                                // if(orderDetail.getOrderType()==Order.ORDER_TYPE_PARTNER){
                                if (orderLocation.getLocationType().equalsIgnoreCase(LocationUtils.TYPE_DELIVERY)) {
                                    deliveryLocation = orderLocation;
                                    LatLng latlng = new LatLng(Double.parseDouble(orderLocation.getLatitude()), Double.parseDouble(orderLocation.getLongitude()));
                                    String locationName = LocationUtils.getAddress(activity, latlng);

                                    if (locationName != null && (!locationName.equals("")))
                                        deliveryLocation.setName(locationName);
                                    else
                                        deliveryLocation.setName(LocationUtils.TYPE_DELIVERY_STRING);

                                    if (orderLocation.getLocationContacts() != null && deliveryLocation.getLocationContacts().size()>0) {
                                        deliveryLocation.setLocationContacts(orderLocation.getLocationContacts());
                                    }
                                }

                            }
                            orderPartner.setDeliveryLocation(deliveryLocation.getName());
                            if (deliveryLocation.getLocationContacts() != null && deliveryLocation.getLocationContacts().size()>0)
                                orderPartner.setDeliveryContact(deliveryLocation.getLocationContacts().get(0).getContactNumber());
                            orderPartnerList.add(orderPartner);
                        }
                        order.setOrderPartners(orderPartnerList);
                        order.setOrderDetails(orderDetail);

                        OrdersTable ordersTable = new OrdersTable();
                        ordersTable.setOrderId(orderDetail.getId());
                        ordersTable.setOrders(order);
//                        ordersTable.save();

                        orders.add(order);

//                        if (order.getOrderDetails().getStatus().equals(Order.RIDER_ACCEPTED) || order.getOrderDetails().getStatus().equals(Order.DELIVERED)) {
//                            if (id.equals(""))
//                                id += String.valueOf(order.getOrderDetails().getId());
//                            else {
//                                if (id.contains(",")) {
//                                    boolean ifExists = false;
//                                    String[] orderIds = id.split(",");
//                                    for (int i = 0; i < orderIds.length; i++) {
//                                        if (order.getOrderDetails().getId().equals(orderIds[i])) {
//                                            ifExists = true;
//                                            break;
//                                        }
//                                    }
//                                    if (ifExists) {
//                                    } else {
//                                        id += "," + order.getOrderDetails().getId();
//                                    }
//                                } else {
//                                    if (id.equals(order.getOrderDetails().getId())) {
//
//                                    } else {
//                                        id += "," + order.getOrderDetails().getId();
//                                    }
//                                }
//                            }
//                        }
//                        if (order.getOrderDetails().getIsAccepted() == null && order.getOrderDetails().getStatus().equals(Order.RIDER_ASSIGNED)) {
//                            hasPending = true;
//                        }
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }


            if (completedOrderDetails != null) {
                for (OrderDetail orderDetail : completedOrderDetails) {

                    try {
                        Order order = new Order();
                        order.setDate(AppUtils.getDate(orderDetail.getDateTimePlacement()));
                        order.setTime(AppUtils.getTime(orderDetail.getDateTimePlacement()));
                        order.setStatusType(orderDetail.getStatus());

                        List<OrderPartners> orderPartnerList = new ArrayList<>();

                        for (OrderComponent orderComponent : orderDetail.getOrderComponents()) {
                            OrderPartners orderPartner = new OrderPartners();
                            orderPartner.setName(orderComponent.getPartnerName());
                            orderPartner.setPackages(orderComponent.getPackages());
                            orderPartner.setPrice(orderComponent.getTotal());
                            orderPartner.setStatus(orderComponent.getStatus());
                            orderPartner.setId(orderComponent.getId());

                            OrderLocation deliveryLocation = null;
                            for (OrderLocation orderLocation : orderComponent.getOrderLocations()) {
                                // if(orderDetail.getOrderType()==Order.ORDER_TYPE_PARTNER){
                                if (orderLocation.getLocationType().equalsIgnoreCase(LocationUtils.TYPE_DELIVERY)) {
                                    deliveryLocation = orderLocation;
                                    LatLng latlng = new LatLng(Double.parseDouble(orderLocation.getLatitude()), Double.parseDouble(orderLocation.getLongitude()));
                                    String locationName = LocationUtils.getAddress(activity, latlng);

                                    if (locationName != null && (!locationName.equals("")))
                                        deliveryLocation.setName(locationName);
                                    else
                                        deliveryLocation.setName(LocationUtils.TYPE_DELIVERY_STRING);

                                    if (orderLocation.getLocationContacts() != null && deliveryLocation.getLocationContacts().size()>0) {
                                        deliveryLocation.setLocationContacts(orderLocation.getLocationContacts());
                                    }
                                }

                            }
                            orderPartner.setDeliveryLocation(deliveryLocation.getName());
                            if (deliveryLocation.getLocationContacts() != null && deliveryLocation.getLocationContacts().size()>0)
                                orderPartner.setDeliveryContact(deliveryLocation.getLocationContacts().get(0).getContactNumber());
                            orderPartnerList.add(orderPartner);
                        }
                        order.setOrderPartners(orderPartnerList);
                        order.setOrderDetails(orderDetail);

                        OrdersTable ordersTable = new OrdersTable();
                        ordersTable.setOrderId(orderDetail.getId());
                        ordersTable.setOrders(order);
              //          ordersTable.save();

                        completedOrders.add(order);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }


            Collections.sort(orders, Order.sortByStatus);
            Collections.sort(completedOrders, Order.sortByStatus);

            return orders;
        }

        @Override
        protected void onPostExecute(List<Order> aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid != null|| completedOrders!=null) {
                responseListenerOrdersListing.onSuccess(calledApi, aVoid, completedOrders);
            }
            else {
                responseListenerOrdersListing.onError(calledApi);
            }
        }

        @Override
        protected void onCancelled(List<Order> aVoid) {
            super.onCancelled(aVoid);
            responseListenerOrdersListing.onError(calledApi);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            responseListenerOrdersListing.onError(calledApi);
        }
    }



//    class BackgroundTask extends AsyncTask<Void, Void, List<Order>> {
//        String ordersJson;
//        List<OrderDetail> orderDetails = null;
//        List<Order> orders = new ArrayList<>();
//
//        public BackgroundTask(String jsonAsString) {
//            this.ordersJson = jsonAsString;
//        }
//
//        @Override
//        protected List<Order> doInBackground(Void... voids) {
//            ExclusionStrategy exclusionStrategy = new ExclusionStrategy() {
//
//                @Override
//                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
//                    return false;
//                }
//
//                @Override
//                public boolean shouldSkipClass(Class<?> clazz) {
//                    return clazz == Field.class || clazz == Method.class;
//                }
//            };
//
//            Gson gson = new GsonBuilder()
//                    .addSerializationExclusionStrategy(exclusionStrategy)
//                    .addDeserializationExclusionStrategy(exclusionStrategy)
//                    .create();
//
//            try {
//                orderDetails = gson.fromJson(String.valueOf(new JSONArray(ordersJson)), new TypeToken<List<OrderDetail>>() {
//                }.getType());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
///*
//            if (orderDetails != null) {
//                for (OrderDetail orderDetail : orderDetails) {
//                    OrderTable order = new OrderTable();
//                    order.setDate(AppUtils.getDate(orderDetail.getDateTimePlacement()));
//                    order.setTime(AppUtils.getTime(orderDetail.getDateTimePlacement()));
//                    order.setStatusType(orderDetail.getStatus());
//
//                    List<OrderPartnerTable> orderPartnerList = new ArrayList<>();
//
//                    for (OrderComponent orderComponent : orderDetail.getOrderComponents()) {
//                        OrderPartnerTable orderPartner = new OrderPartnerTable();
//                        orderPartner.setName(orderComponent.getPartnerName());
//                        orderPartner.setPrice(orderComponent.getTotal());
//                        orderPartner.setStatus(orderComponent.getStatus());
//                        orderPartner.setId(orderComponent.getId());
//
//                        List<PackagesTable> packagesTables = new ArrayList<>();
//
//                        for (Packages pkg: orderComponent.getPackages()) {
//                            PackagesTable packagesTable = new PackagesTable();
//                            packagesTable.setId(pkg.getId());
//                            packagesTable.setPackageType(pkg.getPackageType());
//                            packagesTable.setDiscount(pkg.getDiscount());
//                            packagesTable.setTax(pkg.getTax());
//                            packagesTable.setPrice(pkg.getPrice());
//                            packagesTable.setQuantity(pkg.getQuantity());
//                            packagesTable.setPackageID(pkg.getPackageID());
//                            packagesTable.setPackageName(pkg.getPackageName());
//                            packagesTable.setOrderComponentID(orderComponent.getId() );
//                            packagesTable.setOrderPartnerID(orderComponent.getPartnerID());
//
//                            packagesTables.add(packagesTable);
//                        }
//                        orderPartner.setPackages(packagesTables);
//
//                        OrderLocationTable deliveryLocation = null;
//                        for (OrderLocation orderLocation : orderComponent.getOrderLocations()) {
//                           // if(orderDetail.getOrderType()==Order.ORDER_TYPE_PARTNER){
//                            if (orderLocation.getLocationType().equalsIgnoreCase(LocationUtils.TYPE_DELIVERY)) {
//
//                                deliveryLocation = new OrderLocationTable();
//                                deliveryLocation.setId(orderLocation.getId());
//                                deliveryLocation.setLocationID(orderLocation.getLocationID());
//                                deliveryLocation.setLocationType(orderLocation.getLocationType());
//                                deliveryLocation.setLatitude(orderLocation.getLatitude());
//                                deliveryLocation.setLongitude(orderLocation.getLongitude());
//                                deliveryLocation.setName(orderLocation.getName());
//                                deliveryLocation.setManualLocation(orderLocation.getManualLocation());
//                                deliveryLocation.setOrderID(orderDetail.getId());
//                                deliveryLocation.setOrderComponentID(orderComponent.getId());
//
//                                LatLng latlng = new LatLng(Double.parseDouble(orderLocation.getLatitude()),Double.parseDouble(orderLocation.getLongitude()));
//                                String locationName = LocationUtils.getAddress(activity, latlng);
//
//                                if(locationName!=null&&(!locationName.equals("")))
//                                    deliveryLocation.setName(locationName);
//                                else
//                                    deliveryLocation.setName(LocationUtils.TYPE_DELIVERY_STRING);
//                            }
////                            }
////                            else
////                            {
////                                if (orderLocation.getLocationType().equalsIgnoreCase(LocationUtils.TYPE_PICKUP)) {
////                                    deliveryLocation = orderLocation;
////                                    LatLng latlng = new LatLng(Double.parseDouble(orderLocation.getLatitude()),Double.parseDouble(orderLocation.getLongitude()));
////                                    String locationName = LocationUtils.getAddress(activity, latlng);
////
////                                    if(locationName!=null&&(!locationName.equals("")))
////                                        deliveryLocation.setName(locationName);
////                                    else
////                                        deliveryLocation.setName(LocationUtils.TYPE_PICKUP_STRING);
////                                }
////                            }
//                        }
//                        orderPartner.setDeliveryLocation(deliveryLocation.getName());
//                        orderPartnerList.add(orderPartner);
//                    }
//                    order.setOrderPartners(orderPartnerList);
//
//                    OrderDetailTable orderDetailTable = new OrderDetailTable();
//                    orderDetailTable.setId(order.getOrderDetail().getId());
//                    orderDetailTable.setIsAccepted(order.getOrderDetail().getIsAccepted());
//                    orderDetailTable.setReferenceNumber(order.getOrderDetail().getReferenceNumber());
//                    orderDetailTable.setSpecialInstruction(order.getOrderDetail().getSpecialInstruction());
//                    orderDetailTable.setEstimatedDeliveryFee(order.getOrderDetail().getEstimatedDeliveryFee());
//                    orderDetailTable.setEstimatedDeliveryTime(order.getOrderDetail().getEstimatedDeliveryTime());
//                    orderDetailTable.setOrderAdditionalComment(order.getOrderDetail().getOrderAdditionalComment());
//                    orderDetailTable.setDateTimePlacement(order.getOrderDetail().getDateTimePlacement());
//                    orderDetailTable.setSource(order.getOrderDetail().getSource());
//                    orderDetailTable.setLatitude(order.getOrderDetail().getLatitude());
//                    orderDetailTable.setLongitude(order.getOrderDetail().getLongitude());
//                    orderDetailTable.setStatus(order.getOrderDetail().getStatus());
//                    orderDetailTable.setEstimatedOrderPrice(order.getOrderDetail().getEstimatedOrderPrice());
//                    orderDetailTable.setCustomerID(order.getOrderDetail().getCustomerID());
//                    orderDetailTable.setTakenUpByID(order.getOrderDetail().getTakenUpByID());
//                    orderDetailTable.setUserID(order.getOrderDetail().getUserID());
//                    orderDetailTable.setDiscount(order.getOrderDetail().getDiscount());
//                    orderDetailTable.setSubAmount(order.getOrderDetail().getSubAmount());
//                    orderDetailTable.setTax(order.getOrderDetail().getTax());
//                    orderDetailTable.setTotal(order.getOrderDetail().getTotal());
//                    orderDetailTable.setOrderType(order.getOrderDetail().getOrderType());
//
//                    List<OrderComponentTable> orderComponents = new ArrayList<>();
//
//                    for (OrderComponent orderComponent :orderDetail.getOrderComponents()) {
//
//                        OrderComponentTable orderComponentTable = new OrderComponentTable();
//
//                        orderComponentTable.setId(orderComponent.getId());
//                        orderComponentTable.setStatus(orderComponent.getStatus());
//                        orderComponentTable.setDiscount(orderComponent.getDiscount());
//                        orderComponentTable.setSubAmount(orderComponent.getSubAmount());
//                        orderComponentTable.setTax(orderComponent.getTax());
//                        orderComponentTable.setTotal(orderComponent.getTotal());
//                        orderComponentTable.setOrderID(orderComponent.getOrderID());
//                        orderComponentTable.setPartnerID(orderComponent.getPartnerID());
//                        orderComponentTable.setPartnerName(orderComponent.getPartnerName());
//                        orderComponentTable.setEstimatedOrderPrice(orderComponent.getEstimatedOrderPrice());
//
//                        orderComponents.add(orderComponentTable);
//                    }
//
//                    orderDetailTable.setOrderComponents(orderComponents);
//
//
//                    OrderLocationTable paymentLocation = new OrderLocationTable();
//
//                    paymentLocation.setId(orderDetail.getPaymentLocation().getId());
//                    paymentLocation.setLocationID(orderDetail.getPaymentLocation().getLocationID());
//                    paymentLocation.setLocationType(orderDetail.getPaymentLocation().getLocationType());
//                    paymentLocation.setLatitude(orderDetail.getPaymentLocation().getLatitude());
//                    paymentLocation.setLongitude(orderDetail.getPaymentLocation().getLongitude());
//                    paymentLocation.setName(orderDetail.getPaymentLocation().getName());
//                    paymentLocation.setManualLocation(orderDetail.getPaymentLocation().getManualLocation());
//                    paymentLocation.setOrderID(orderDetail.getId());
//                    paymentLocation.setOrderComponentID(orderDetail.getPaymentLocation().getId());
//
//                    LatLng latlng = new LatLng(Double.parseDouble(orderDetail.getPaymentLocation().getLatitude()),Double.parseDouble(orderDetail.getPaymentLocation().getLongitude()));
//                    String paymentLocationName = LocationUtils.getAddress(activity, latlng);
//
//                    if(paymentLocationName !=null&&(!paymentLocationName .equals("")))
//                        paymentLocation.setName(paymentLocationName );
//                    else
//                        paymentLocation.setName(LocationUtils.TYPE_DELIVERY_STRING);
//                    orderDetailTable.setPaymentLocation(paymentLocation);
//
//                    CustomerTable customerTable = new CustomerTable();
//                    customerTable.setId(orderDetail.getCustomer().getId());
//                    customerTable.setUserName(orderDetail.getCustomer().getUserName());
//                    customerTable.setEmail(orderDetail.getCustomer().getEmail());
//                    customerTable.setMsisdn(orderDetail.getCustomer().getMsisdn());
//                    customerTable.setOrderID(orderDetail.getId());
//
//                    orderDetailTable.setCustomer(customerTable);
//
//
//                    RiderTable riderTableTable = new RiderTable();
//                    riderTableTable.setId(orderDetail.getRider().getId());
//                    riderTableTable.setUserName(orderDetail.getRider().getUserName());
//                    riderTableTable.setEmail(orderDetail.getRider().getEmail());
//                    riderTableTable.setMsisdn(orderDetail.getRider().getMsisdn());
//                    riderTableTable.setOrderID(orderDetail.getId());
//
//                    orderDetailTable.setRider(riderTableTable);
//
//                }
//            }
//*/
//
//
//            if (orderDetails != null) {
//                for (OrderDetail orderDetail : orderDetails) {
//
//                    try {
//                        Order order = new Order();
//                        order.setDate(AppUtils.getDate(orderDetail.getDateTimePlacement()));
//                        order.setTime(AppUtils.getTime(orderDetail.getDateTimePlacement()));
//                        order.setStatusType(orderDetail.getStatus());
//
//                        List<OrderPartners> orderPartnerList = new ArrayList<>();
//
//                        for (OrderComponent orderComponent : orderDetail.getOrderComponents()) {
//                            OrderPartners orderPartner = new OrderPartners();
//                            orderPartner.setName(orderComponent.getPartnerName());
//                            orderPartner.setPackages(orderComponent.getPackages());
//                            orderPartner.setPrice(orderComponent.getTotal());
//                            orderPartner.setStatus(orderComponent.getStatus());
//                            orderPartner.setId(orderComponent.getId());
//
//                            OrderLocation deliveryLocation = null;
//                            for (OrderLocation orderLocation : orderComponent.getOrderLocations()) {
//                                // if(orderDetail.getOrderType()==Order.ORDER_TYPE_PARTNER){
//                                if (orderLocation.getLocationType().equalsIgnoreCase(LocationUtils.TYPE_DELIVERY)) {
//                                    deliveryLocation = orderLocation;
//                                    LatLng latlng = new LatLng(Double.parseDouble(orderLocation.getLatitude()), Double.parseDouble(orderLocation.getLongitude()));
//                                    String locationName = LocationUtils.getAddress(activity, latlng);
//
//                                    if (locationName != null && (!locationName.equals("")))
//                                        deliveryLocation.setName(locationName);
//                                    else
//                                        deliveryLocation.setName(LocationUtils.TYPE_DELIVERY_STRING);
//
//                                    if (orderLocation.getLocationContacts() != null && deliveryLocation.getLocationContacts().size()>0) {
//                                        deliveryLocation.setLocationContacts(orderLocation.getLocationContacts());
//                                    }
//                                }
////                            }
////                            else
////                            {
////                                if (orderLocation.getLocationType().equalsIgnoreCase(LocationUtils.TYPE_PICKUP)) {
////                                    deliveryLocation = orderLocation;
////                                    LatLng latlng = new LatLng(Double.parseDouble(orderLocation.getLatitude()),Double.parseDouble(orderLocation.getLongitude()));
////                                    String locationName = LocationUtils.getAddress(activity, latlng);
////
////                                    if(locationName!=null&&(!locationName.equals("")))
////                                        deliveryLocation.setName(locationName);
////                                    else
////                                        deliveryLocation.setName(LocationUtils.TYPE_PICKUP_STRING);
////                                }
////                            }
//                            }
//                            orderPartner.setDeliveryLocation(deliveryLocation.getName());
//                            if (deliveryLocation.getLocationContacts() != null && deliveryLocation.getLocationContacts().size()>0)
//                                orderPartner.setDeliveryContact(deliveryLocation.getLocationContacts().get(0).getContactNumber());
//                            orderPartnerList.add(orderPartner);
//                        }
//                        order.setOrderPartners(orderPartnerList);
//                        order.setOrderDetails(orderDetail);
//
//                        OrdersTable ordersTable = new OrdersTable();
//                        ordersTable.setOrderId(orderDetail.getId());
//                        ordersTable.setOrders(order);
//                        ordersTable.save();
//
//                        orders.add(order);
//                    }catch(Exception ex){
//                        ex.printStackTrace();
//                    }
//                }
//            }
//
//
//            Collections.sort(orders, Order.sortByStatus);
//
//            return orders;
//        }
//
//        @Override
//        protected void onPostExecute(List<Order> aVoid) {
//            super.onPostExecute(aVoid);
//            if (aVoid != null) {
//                responseListenerOrdersListing.onSuccess(calledApi, aVoid);
//            } else {
//                responseListenerOrdersListing.onError(calledApi);
//            }
//        }
//
//        @Override
//        protected void onCancelled(List<Order> aVoid) {
//            super.onCancelled(aVoid);
//            responseListenerOrdersListing.onError(calledApi);
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//            responseListenerOrdersListing.onError(calledApi);
//        }
//    }
//

}
