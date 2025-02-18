package com.darewro.rider.data.api;

public class ApiCalls {

    private static final String FIREBASE_KEY_RIDERS_LIVE = "Riders";
    private static final String FIREBASE_KEY_ORDERS_LIVE = "Orders";
    private static final String FIREBASE_KEY_ORDERS_DEV = "TestOrders";
    private static final String FIREBASE_KEY_RIDERS_DEV = "TestRiders";


    /*LIVE*/
//    private static final String BASE_URL_AZURE = "http://ddspk.dyndns.org:81/api/";
//    private static final String BASE_URL_STAGING = "http://darewrochat.southeastasia.cloudapp.azure.com/api/";
//
//    private static final String BASE_URL_IMAGE_AZURE = "http://ddspk.dyndns.org:81/";
//    public static final String BASE_URL_IMAGE_NOTIFICATION = "http://ddspk.dyndns.org:81/user/";
//    public static final String BASE_URL_VOICE_NOTE_AZURE = "http://ddspk.dyndns.org:81";
//    public static final String BASE_URL_CHAT = "http://ddspk.dyndns.org:81";


//    private static final String BASE_URL_AZURE = "http://dds.myfirewall.co:81/api/";
//    private static final String BASE_URL_STAGING = "http://darewrochat.southeastasia.cloudapp.azure.com/api/";
//
//    private static final String BASE_URL_IMAGE_AZURE = "http://dds.myfirewall.co:81/";
//    public static final String BASE_URL_IMAGE_NOTIFICATION = "http://dds.myfirewall.co:81/user/";
//    public static final String BASE_URL_VOICE_NOTE_AZURE = "http://dds.myfirewall.co:81";
//    public static final String BASE_URL_CHAT = "http://dds.myfirewall.co:81";

    private static final String BASE_URL_AZURE = "http://58.65.153.34:81/api/";
    //private static final String BASE_URL_STAGING = "http://darewrochat.southeastasia.cloudapp.azure.com/api/";

    private static final String BASE_URL_IMAGE_AZURE = "http://58.65.153.34:81/";
    public static final String BASE_URL_IMAGE_NOTIFICATION = "http://58.65.153.34:81/user/";
    public static final String BASE_URL_VOICE_NOTE_AZURE = "http://58.65.153.34:81";
    public static final String BASE_URL_CHAT = "http://58.65.153.34:81";


//    private static final String BASE_URL_AZURE = "http://darewro.southeastasia.cloudapp.azure.com/api/";
//    private static final String BASE_URL_STAGING = "http://darewrochat.southeastasia.cloudapp.azure.com/api/";
//
//    private static final String BASE_URL_IMAGE_AZURE = "http://darewro.southeastasia.cloudapp.azure.com/";
//    public static final String BASE_URL_IMAGE_NOTIFICATION = "http://darewro.southeastasia.cloudapp.azure.com/user/";
//    public static final String BASE_URL_VOICE_NOTE_AZURE = "http://darewro.southeastasia.cloudapp.azure.com";
//    public static final String BASE_URL_CHAT = "http://darewro.southeastasia.cloudapp.azure.com";

    //    public static  String BASE_URL = BASE_URL_STAGING;
    public static  String BASE_URL = BASE_URL_AZURE;
    public static  String BASE_URL_IMAGE = BASE_URL_IMAGE_AZURE;
    public static final String FIREBASE_KEY_RIDERS = FIREBASE_KEY_RIDERS_LIVE;
    public static final String FIREBASE_KEY_ORDERS = FIREBASE_KEY_ORDERS_LIVE;




    //    private static final String BASE_URL = BASE_URL_STAGING_VM;
//    private static final String BASE_URL_IMAGE= BASE_URL_IMAGE_STAGING;
//    public static final String FIREBASE_KEY_ORDERS = FIREBASE_KEY_ORDERS_DEV;
//    public static final String FIREBASE_KEY_RIDERS = FIREBASE_KEY_RIDERS_DEV;
    /*STAGING*/
    private static final String BASE_URL_STAGING_VM = "http://10.1.254.31:19000/api/";
    private static final String BASE_URL_IMAGE_STAGING = "http://10.1.254.31:19000/";
    /*DEV*/
    private static final String BASE_URL_DEV_AHMEDZEB_NGROK = "http://4c4ba361fc13.ngrok.io/api/";//Ahmed Zeb Static VPN Laptop IP
    private static final String BASE_URL_IMAGE_DEV_AHMEDZEB_NGROK = "http://4c4ba361fc13.ngrok.io/";//Ahmed Zeb Static VPN Laptop IP
//    public static String BASE_URL = BASE_URL_DEV_AHMEDZEB_NGROK;
//    public static String BASE_URL_IMAGE = BASE_URL_IMAGE_DEV_AHMEDZEB_NGROK;
//    public static final String FIREBASE_KEY_ORDERS = FIREBASE_KEY_ORDERS_DEV;
//    public static final String FIREBASE_KEY_RIDERS = FIREBASE_KEY_RIDERS_DEV;





    private static final String BASE_URL_DEV_VM = "http://10.10.201.20:19000/api/";//Ahmed Zeb Static VPN Laptop IP
    private static final String BASE_URL_IMAGE_DEV = "http://10.10.201.20:19000/";//Ahmed Zeb Static VPN Laptop IP
    /*TEST*/
    private static final String BASE_URL_TEST_VM = "http://10.1.250.183:18000/api/";
    private static final String BASE_URL_IMAGE_TEST = "http://10.1.250.183:18000/";
    //    private static final String BASE_URL_DEV_VM = "http://192.168.1.100:19000/api/";//Ahmed Zeb Static Laptop IP
//    private static final String BASE_URL_IMAGE_DEV = "http://192.168.1.100:19000/";//Ahmed Zeb Static Laptop IP
//    private static final String BASE_URL_DEV_VM_AHMAD_ZEB = "http://10.11.203.54:19000/api/";
//    private static final String BASE_URL_DEV_VM = "http://10.1.250.183/api/";
//    private static final String BASE_URL_IMAGE_DEV = "http://10.1.250.183/";
//    private static final String BASE_URL = BASE_URL_DEV_VM_AHMAD_ZEB;
//    private static final String BASE_URL = BASE_URL_DEV_VM;
//    private static final String BASE_URL_IMAGE = BASE_URL_IMAGE_DEV;
//    public static final String FIREBASE_KEY_ORDERS = FIREBASE_KEY_ORDERS_DEV;
//    public static final String FIREBASE_KEY_RIDERS = FIREBASE_KEY_RIDERS_DEV;
    /*URLS*/
//    private static final String BASE_URL = BASE_URL_TEST_VM;
//    public static final String FIREBASE_KEY_ORDERS = FIREBASE_KEY_ORDERS_DEV;
    //    private static final String BASE_URL_IMAGE = BASE_URL_IMAGE_TEST;
    //    public static final String FIREBASE_KEY_RIDERS = FIREBASE_KEY_RIDERS_DEV;
    private static final String GET_TOKEN = "Token";
    private static final String LOGIN_RIDER = "User/RiderSignUp";
    private static final String ORDER_LISTING = "Composer/GetUserOrders";
    private static final String NEW_ORDER_LISTING = "Composer/GetUsersNewAndCompletedOrders";

    private static final String ALL_ORDER_LISTING = "Composer/GetOrdersByStatus";

    private static final String ORDER_DETAILS = "Composer/GetOrderDetails";
    private static final String CHANGE_STATUS = "User/SetRiderAvailability";
    private static final String CHANGE_ORDER_STATUS = "Order/SetOrderAcceptance";

    private static final String ASSIGN_ORDER_TO_RIDER = "Composer/AssignOrderToRider";
    private static final String DELIVER_ORDER = "Composer/DeliverOrder";
    private static final String INVOICE_PAYMENT = "Composer/ReceiveOrderInvoicePayment";
    private static final String UPDATE_RIDER_DEVICE = "User/UpdateRiderDevice";
    private static final String UPDATE_ORDER_COMPONENT_STATUS = "Order/SetOrderComponentStatus";
    private static final String GET_INVOICE = "Finance/GetInvoiceByOrderID";
    private static final String RATE_API = "Rating/RateEntity";
    private static final String SAVE_RIDER_LOCATION = "User/SaveRiderLocation";
    private static final String RIDER_TRACKING = "composer/RiderTracking";
//    private static final String RIDER_DASHBOARD = "Composer/GetRiderBonus";
    private static final String RIDER_DASHBOARD = "Composer/GetBonusForRider";
    private static final String RIDER_DASHBOARD_STATS = "Composer/GetRiderStats";
    private static final String FILE_UPLOAD = "user/SaveChatFile";
    //public static final String DELETE_ORDER = "https://us-central1-darewro-224406.cloudfunctions.net/deleteOrder";
    public static final String NEAREST_ORDERS = "Composer/GetNewOrPlacedOrdersForRiders";
    public static final String GET_NOTIFICATIONS = "user/GetRiderNotifications";
    public static final String GET_RECORDING = "composer/GetCustomerCallRecording";
    public static String CHANNEL_ID = "my_channel_01";


    public static String getRiderDashboard() {
        return BASE_URL + RIDER_DASHBOARD;
    }

    public static String getRiderDashboardStats() {
        return BASE_URL + RIDER_DASHBOARD_STATS;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getGetToken() {
        return BASE_URL + GET_TOKEN;
    }

    public static String getImageUrl(String url) {
        return BASE_URL_IMAGE + url;
    }

    public static String getOrderListing() {
        return BASE_URL + ORDER_LISTING;
    }

    public static String getNewOrderListing() {
        return BASE_URL + NEW_ORDER_LISTING;
    }

    public static String getAllOrderListing() {
        return BASE_URL + ALL_ORDER_LISTING;
    }
    public static String assignOrderToRider() {
        return BASE_URL + ASSIGN_ORDER_TO_RIDER;
    }


    public static String getOrderDetails() {
        return BASE_URL + ORDER_DETAILS;
    }

    public static String changeStatus() {
        return BASE_URL + CHANGE_STATUS;
    }

    public static String changeOrderStatus() {
        return BASE_URL + CHANGE_ORDER_STATUS;
    }

    public static String deliverOrder() {
        return BASE_URL + DELIVER_ORDER;
    }

    public static String invoicePayment() {
        return BASE_URL + INVOICE_PAYMENT;
    }

    public static String riderLogin() {
        return BASE_URL + LOGIN_RIDER;
    }

    public static String updateRiderDevice() {
        return BASE_URL + UPDATE_RIDER_DEVICE;
    }

    public static String getInvoice() {
        return BASE_URL + GET_INVOICE;
    }

    public static String rateApi() {
        return BASE_URL + RATE_API;
    }

    public static String updateOrderComponentStatus() {
        return BASE_URL + UPDATE_ORDER_COMPONENT_STATUS;
    }

    public static String saveOrderLocation() {
        return BASE_URL + SAVE_RIDER_LOCATION;
    }

    public static String saveRiderLocation() {
        return BASE_URL + RIDER_TRACKING;
    }

    public static String getNearestOrders() {
        return BASE_URL + NEAREST_ORDERS;
    }

    public static String getFileUpload() {
        return BASE_URL + FILE_UPLOAD;
    }


    public static String getNotifications() {
        return BASE_URL + GET_NOTIFICATIONS;
    }

    public static String getVoiceNote() { return BASE_URL + GET_RECORDING; }
}
