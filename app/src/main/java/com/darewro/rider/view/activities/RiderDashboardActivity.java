package com.darewro.rider.view.activities;

import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.darewro.rider.R;
import com.darewro.rider.data.api.ApiCalls;
import com.darewro.rider.data.api.Interface.InitApi;
import com.darewro.rider.data.api.handlers.DashboardHandler;
import com.darewro.rider.data.api.handlers.OrdersListingHandler;
import com.darewro.rider.data.api.models.Dashboard;
import com.darewro.rider.data.api.requests.JsonObjectRequestCall;
import com.darewro.rider.presenter.ResponseListenerDashboard;
import com.darewro.rider.presenter.ResponseListenerOrdersListing;
import com.darewro.rider.view.customViews.RoundedEdgesImageView;
import com.darewro.rider.view.listeners.DateUpdateListener;
import com.darewro.rider.view.models.Order;
import com.darewro.rider.view.utils.AlertDialogUtils;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.SharedPreferenceHelper;


import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RiderDashboardActivity extends BaseFullScreenActivity implements ResponseListenerDashboard, DateUpdateListener, ResponseListenerOrdersListing {

    TextView name, msisdn, bikeno, rating, shift, rejectionCount;// total_online_time, todays_orders_pending, todays_orders_completed, todays_orders_total;
    RoundedEdgesImageView picture;

    TextView cnic,txtnewOrders, txtcompletedOrders, txtcancancelledOrders, txttotalOrders, txtonlineTiming, txttodayTrips, txtextraTripsBonus, txtmultipleOrderBonus,
    longDistanceBonus, txttripBonus, txtadjustmentAllowance, txtacceptanceRateToday, txtcustomerRatingToday, totalKmTravelledToday, totalEarningOfDay,
    outstandingAmount, rejected_amount_granted, rejected_amount_deducted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_dashboard);
        initializeViews();
    }

    @Override
    public void initializeViews() {

        name = findViewById(R.id.name);
        cnic = findViewById(R.id.cnic);
        msisdn = findViewById(R.id.msisdn);
        bikeno = findViewById(R.id.bike_no);
        rating = findViewById(R.id.rating);
        shift = findViewById(R.id.shift);
        rejectionCount = findViewById(R.id.rejectionCount);
        picture = findViewById(R.id.profile_pic);
        txtnewOrders = findViewById(R.id.todays_orders_pending);
        txtcompletedOrders = findViewById(R.id.todays_orders_completed);
        txtcancancelledOrders = findViewById(R.id.todays_orders_cancelled);
        txttotalOrders = findViewById(R.id.todays_orders_total);

        txtonlineTiming = findViewById(R.id.total_online_time);
        txttodayTrips = findViewById(R.id.today_trips);
        txtextraTripsBonus = findViewById(R.id.extra_trip_bonus);
        txtmultipleOrderBonus = findViewById(R.id.multiple_order_bonus);
        longDistanceBonus = findViewById(R.id.long_distance_bonus);
        txtacceptanceRateToday = findViewById(R.id.acceptance_rate_today);
        txtcustomerRatingToday = findViewById(R.id.customer_rating_today);
        totalKmTravelledToday = findViewById(R.id.total_km_travelled);
        totalEarningOfDay = findViewById(R.id.total_earning_of_day);
        outstandingAmount = findViewById(R.id.oustanding_amount);
        txtadjustmentAllowance = findViewById(R.id.adjustment_allowance);
        txttripBonus = findViewById(R.id.trip_bonus);
        rejected_amount_deducted = findViewById(R.id.rejected_amount_deducted);
        rejected_amount_granted = findViewById(R.id.rejected_amount_granted);

        AppUtils.setMontserrat(name);
        AppUtils.setMontserrat(cnic);
        AppUtils.setMontserrat(msisdn);
        AppUtils.setMontserrat(bikeno);
        AppUtils.setMontserrat(rating);
        AppUtils.setMontserrat(shift);
        AppUtils.setMontserrat(rejectionCount);
        AppUtils.setMontserrat(txtnewOrders);
        AppUtils.setMontserrat(txtcompletedOrders);
        AppUtils.setMontserrat(txtcancancelledOrders);
        AppUtils.setMontserrat(txttotalOrders);
        AppUtils.setMontserrat(txtonlineTiming);
        AppUtils.setMontserrat(txttodayTrips);
        AppUtils.setMontserrat(txtextraTripsBonus);
        AppUtils.setMontserrat(txtmultipleOrderBonus);
        AppUtils.setMontserrat(longDistanceBonus);
        AppUtils.setMontserrat(txtacceptanceRateToday);
        AppUtils.setMontserrat(txtcustomerRatingToday);
        AppUtils.setMontserrat(totalKmTravelledToday);
        AppUtils.setMontserrat(totalEarningOfDay);
        AppUtils.setMontserrat(outstandingAmount);
        AppUtils.setMontserrat(txtadjustmentAllowance);
        AppUtils.setMontserrat(txttripBonus);
        AppUtils.setMontserrat(rejected_amount_deducted);
        AppUtils.setMontserrat(rejected_amount_granted);
        //setDateRange();

      //  User user = SharedPreferenceHelper.getUser(RiderDashboardActivity.this);
//
//        name.setText(user.getUserName());
//        msisdn.setText(user.getMsisdn());
//        bikeno.setText(user.getBikeNumber());
//        rating.setText(user.getRating());
//        shift.setText(user.getRiderShiftSettings());
//
//        try {
//            JSONObject json = new JSONObject(user.getRiderShiftSettings());
//            String startTIming = json.getString("riderStartTime");
//            String endTIming = json.getString("riderEndTime");
//
//            String[] start = startTIming.split(" ")[0].split(":");
//            for (int i = 0; i < start.length; i++) {
//                if (start[i].length() <= 1) {
//                    start[i] = "0" + start[i];
//                }
//            }
//            String newStart = "";
//            for (int i = 0; i < start.length; i++) {
//                if (i < start.length - 1) {
//                    newStart += start[i] + ":";
//                } else {
//                    newStart += start[i] + " " + startTIming.split(" ")[1];
//                }
//            }
//
//            String[] end = endTIming.split(" ")[0].split(":");
//            for (int i = 0; i < end.length; i++) {
//                if (end[i].length() <= 1) {
//                    end[i] = "0" + end[i];
//                }
//            }
//
//            String newEnd = "";
//            for (int i = 0; i < end.length; i++) {
//                if (i < end.length - 1) {
//                    newEnd += end[i] + ":";
//                } else {
//                    newEnd += end[i] + " " + endTIming.split(" ")[1];
//                }
//            }
//
//            String shiftTimings = AppUtils.getConvertedDateFromOneFormatToOther(newStart, AppUtils.FORMAT17, AppUtils.FORMAT5) + " - " + AppUtils.getConvertedDateFromOneFormatToOther(newEnd, AppUtils.FORMAT17, AppUtils.FORMAT5);
//
//            shift.setText(shiftTimings);
//
//        } catch (JSONException exc) {
//            exc.printStackTrace();
//        }


//        if (user.isAvailable()) {
//            picture.setToSquareWithRoundedCorneredBorder(getResources().getColor(R.color.green), 2);
//        } else {
//            picture.setToSquareWithRoundedCorneredBorder(getResources().getColor(R.color.grey2), 2);
//        }
/*
        if(user.getPicturePath()!=null&&user.getPicturePath().length()>0) {
            AppUtils.loadPicture(RiderDashboardActivity.this,picture,null,user.getPicturePath());
            Glide.with(RiderDashboardActivity.this).asBitmap().load(user.getPicturePath()).into(picture);
        }
        else
        {
            Glide.with(RiderDashboardActivity.this).asBitmap().load(getResources().getDrawable(R.drawable.ic_profile_pic)).into(picture);
        }*/
        setListeners();
    }

    @Override
    public void setListeners() {

    }

    @Override
    public void handleIntent() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();

    }

    public void loadData() {
       // if (sdate.getText().toString() != null) {
           // String startDate = AppUtils.getConvertedDateFromOneFormatToOther(AppUtils.getCurrentDate_dd_mm_yyyy(), AppUtils.FORMAT15, AppUtils.FORMAT3);
            getRiderDashboardDetails(null, null);
       // }

//        if(AppUtils.isNetworkConnected(RiderDashboardActivity.this))
//        getOrders(0, 0, "", false);

    }

    public void getOrders(int pageIndex, int limit, String search, boolean IsCustomer) {

        AlertDialogUtils.getInstance().showLoading(RiderDashboardActivity.this);

        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {
                return null;
            }

            @Override
            public HashMap<String, Object> getObjBody() {
                return null;
            }

            @Override
            public HashMap<String, String> getHeader() {
                return AppUtils.getStandardHeaders(RiderDashboardActivity.this);
            }
        };

        //String getPartners = ApiCalls.getNewOrderListing() + "?UserID=99388" + "&IsCustomer=" + IsCustomer;
        String getPartners = ApiCalls.getNewOrderListing() + "?UserID=" + SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, RiderDashboardActivity.this) + "&IsCustomer=" + IsCustomer;
        OrdersListingHandler ordersListingHandler = new OrdersListingHandler(RiderDashboardActivity.this, getPartners, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), getPartners, Request.Method.GET, RiderDashboardActivity.this, ordersListingHandler);
        jsonObjectRequestCall.sendData(false);
    }
    public void getRiderDashboardDetails(String startdate, String endDate) {

        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {
                return null;
            }

            @Override
            public HashMap<String, Object> getObjBody() {
                return null;
            }

            @Override
            public HashMap<String, String> getHeader() {
                return AppUtils.getStandardHeaders(RiderDashboardActivity.this);
            }

        };

        String userId = SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, RiderDashboardActivity.this); //89207
//        String getDashboardDetails = ApiCalls.getRiderDashboard() + "?UserID=" + userId + "&RiderId=" + userId + "&startdate=" + startdate + "&enddate=" + endDate;
//        String getDashboardDetails = ApiCalls.getRiderDashboard() + "?Riderid=" + userId + "&fromDate=" + "null" + "&toDate=" + "null&IsWeeklyBonusCalucation="+true;
//        String getDashboardDetails = ApiCalls.getRiderDashboardStats() + "?Riderid=103585";// + "&fromDate=" + "null" + "&toDate=" + "null&IsWeeklyBonusCalucation="+true;
        String getDashboardDetails = ApiCalls.getRiderDashboardStats() + "?Riderid=" + userId;// + "&fromDate=" + "null" + "&toDate=" + "null&IsWeeklyBonusCalucation="+true;
        DashboardHandler dashboardHandler = new DashboardHandler(RiderDashboardActivity.this, getDashboardDetails, RiderDashboardActivity.this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), getDashboardDetails, Request.Method.GET, RiderDashboardActivity.this, dashboardHandler);
        jsonObjectRequestCall.sendData(true);

    }

    @Override
    public void onSuccess(String calledApi, Dashboard dashboard) {
        if (dashboard != null) {
//            totalOrder.setText(String.valueOf(Math.round(dashboard.getTotalOrders())));
//            totalRevenue.setText(String.valueOf(Math.round(dashboard.getTotalDeliveryFee())));
//            applicableBonus.setText(String.valueOf(Math.round((dashboard.getTotalBonus()-dashboard.getRiderBonusDailyTargetedRevenueGranted()))));
//            currentBonus.setText(String.valueOf(Math.round(dashboard.getRiderBonusDailyTargetedRevenueGranted())));
//            total_online_time.setText(dashboard.getOnlineTime()+" Hrs.");

//            Log.d("Log",dashboard.getOrderRejectionCountSettings());
            name.setText(dashboard.getName()+"");
            cnic.setText(dashboard.getCnic()+"");
            bikeno.setText(dashboard.getBikeNumber()+"");
            rating.setText(dashboard.getRating()+"");
            shift.setText(dashboard.getShiftTimings()+"");
            msisdn.setText(dashboard.getMsisdn()+"");

            JSONArray rejectionCountSettings = null;
            try {
                rejectionCountSettings = new JSONArray(dashboard.getOrderRejectionCountSettings());

                String rejectionCountStr = "";
                if (rejectionCountSettings != null && rejectionCountSettings.length() > 0) {

                    //rejectionCountStr+= "<b><u>Order Reject Limit</u></b><br><br>";
                    for(int i=0;i<rejectionCountSettings.length();i++){

                        Log.d("Log",rejectionCountSettings.getJSONObject(i).toString());

                        String orderCount = rejectionCountSettings.getJSONObject(i).getString("orderCount");
                        String monthlyOrderRejectionCount = rejectionCountSettings.getJSONObject(i).getString("monthlyOrderRejectionCount");

                         rejectionCountStr+="Order : "+ orderCount+" / "+monthlyOrderRejectionCount+"<br>";
//                        if(i==1) rejectionCountStr+="Double Order : "+ orderCount+" / "+monthlyOrderRejectionCount+"<br>";
//                        if(i==2) rejectionCountStr+="Triple Order : "+ orderCount+" / "+monthlyOrderRejectionCount+"";
 //                       if(i==1) rejectionCountStr+="2 Orders : "+ rejectionCountSettings.getJSONObject(i).get("orderCount")+"/"+rejectionCountSettings.getJSONObject(i).get("monthlyOrderRejectionCount"+"\n");
  //                      if(i==2) rejectionCountStr+="3 Orders : "+ rejectionCountSettings.getJSONObject(i).get("orderCount")+"/"+rejectionCountSettings.getJSONObject(i).get("monthlyOrderRejectionCount"+"\n");
                    }
                    rejectionCount.setText(Html.fromHtml(rejectionCountStr));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }




//            txtnewOrders.setText(dashboard.getNewOrders()+"");


            txtnewOrders.setText(""+dashboard.getNewOrders()+"");
            txtcompletedOrders.setText(""+dashboard.getCompletedOrders()+"");
            txtcancancelledOrders.setText(""+dashboard.getCancelledOrders()+"");
            txttotalOrders.setText(""+dashboard.getTotalOrders()+"");

            txtonlineTiming.setText(dashboard.getOnlineTiming()+" ");
            txttodayTrips.setText(dashboard.getTodayTrips()+"");
            txtextraTripsBonus.setText(""+dashboard.getExtraTripsBonus()+"");
            txtmultipleOrderBonus.setText(""+dashboard.getMultipleOrderBonus()+" ");
            longDistanceBonus.setText(""+dashboard.getLongDistanceBonus()+"");
            txtacceptanceRateToday.setText(dashboard.getAcceptanceRateToday()+"");
            txtcustomerRatingToday.setText(dashboard.getCustomerRatingToday()+"");
            totalKmTravelledToday.setText(dashboard.getTotalKmTravelledToday()+"");
            totalEarningOfDay.setText(""+dashboard.getTotalEarningOfDay()+"");
            outstandingAmount.setText(""+dashboard.getOutstandingAmount()+"");
            txtadjustmentAllowance.setText(""+dashboard.getAdjustmentAllowance()+"");
            txttripBonus.setText(""+dashboard.getTripBonus()+"");
            rejected_amount_deducted.setText(""+dashboard.getRejectionAmountToDeduct());
            rejected_amount_granted.setText(""+dashboard.getRejectedOrdersAmountGranted());
            if (dashboard.getPicturePath() == null || (dashboard.getPicturePath() != null && dashboard.getPicturePath().equals(""))) {

            } else {
                AppUtils.loadPicture(RiderDashboardActivity.this, picture, ApiCalls.getImageUrl(dashboard.getPicturePath()));
            }
        }
    }

    @Override
    public void onSuccess(String calledApi) {

    }

    @Override
    public void onError(String calledApi) {

    }

    @Override
    public void onError(String calledApi, int errorCode) {
        if(errorCode == 403)
            AlertDialogUtils.showAlertDialog(RiderDashboardActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), getString(R.string.app_update), getString(R.string.ok), null, true);

    }

    @Override
    public void onDateUpdated(View view, String date) {
        ((TextView) view).setText(date);
        loadData();
    }

    private void setDateRange()
    {
        String current_date = AppUtils.getCurrentDate_dd_mm_yyyy();

        Calendar c = Calendar.getInstance(Locale.US);
        Date da = AppUtils.getDateFromString(current_date,AppUtils.FORMAT15);
        c.setTime(da);
        c.setTimeZone(TimeZone.getTimeZone("PST"));

//        int day = c.get(c.DAY_OF_WEEK);
//        c.add(Calendar.DAY_OF_MONTH, -day+1);
//        String first_date_of_week= AppUtils.getStringFromDate(c.getTime(),AppUtils.FORMAT15);

        c.add(Calendar.DATE, -1);

        String yesterday_date = AppUtils.getStringFromDate(c.getTime(),AppUtils.FORMAT15);
//        sdate.setText(yesterday_date);
//        cycle_date.setText(yesterday_date);

//        edate.setText(yesterday_date);


    }

    private void initAdapter(List<Order> orders) {
        int newTabList = 0;
        int completedTabList = 0;

        Collections.sort(orders, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return Integer.valueOf(o1.getStatusType()).compareTo(Integer.valueOf(o2.getStatusType()));
            }
        });

        for (int i = 0; i < orders.size(); i++) {
            if (Integer.parseInt(orders.get(i).getStatusType()) < Integer.parseInt(Order.PAYMENT_RECEIVED)) {
                if(orders.get(i).getDate()!=null){
                if(DateUtils.isToday((AppUtils.getDateStamp(orders.get(i).getDate()).getTime())))
                newTabList++;}
            } else {
                if(orders.get(i).getDate()!=null){
                if(DateUtils.isToday((AppUtils.getDateStamp(orders.get(i).getDate()).getTime())))
                completedTabList++;}
            }
        }


        final int finalNewOrders = newTabList;
        final int finalCompletedOrders = completedTabList;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                todays_orders_pending.setText("New\n"+finalNewOrders);
//                todays_orders_completed.setText("Completed\n"+finalCompletedOrders);
//                todays_orders_total.setText("Total\n"+(finalNewOrders + finalCompletedOrders));

            }
        });

    }

    @Override
    public void onSuccess(String calledApi, String json) {

    }

    @Override
    public void onSuccess(String calledApi, final List<Order> orders) {
//        populateData();
        AlertDialogUtils.getInstance().hideLoading();
        initAdapter(orders);

    }

    @Override
    public void onSuccess(String calledApi, List<Order> orders, List<Order> completedOrders) {
        AlertDialogUtils.getInstance().hideLoading();

        int newTabList = 0;
        int completedTabList = 0;

//        Collections.sort(orders, new Comparator<Order>() {
//            @Override
//            public int compare(Order o1, Order o2) {
//                return Integer.valueOf(o1.getStatusType()).compareTo(Integer.valueOf(o2.getStatusType()));
//            }
//        });

        for (int i = 0; i < orders.size(); i++) {
            if (Integer.parseInt(orders.get(i).getStatusType()) < Integer.parseInt(Order.PAYMENT_RECEIVED)) {
                if(orders.get(i).getDate()!=null){
                    if(DateUtils.isToday((AppUtils.getDateStamp(orders.get(i).getDate()).getTime())))
                        newTabList++;}
            } else {
                if(orders.get(i).getDate()!=null){
                    if(DateUtils.isToday((AppUtils.getDateStamp(orders.get(i).getDate()).getTime())))
                        completedTabList++;}
            }
        }

        for (int i = 0; i < completedOrders.size(); i++) {
            if (Integer.parseInt(completedOrders.get(i).getStatusType()) < Integer.parseInt(Order.PAYMENT_RECEIVED)) {
                if(completedOrders.get(i).getDate()!=null){
                    if(DateUtils.isToday((AppUtils.getDateStamp(completedOrders.get(i).getDate()).getTime())))
                        newTabList++;
                }
            } else {
                if(completedOrders.get(i).getDate()!=null){
                    if(DateUtils.isToday((AppUtils.getDateStamp(completedOrders.get(i).getDate()).getTime())))
                        completedTabList++;}
            }
        }


        final int finalNewOrders = newTabList;
        final int finalCompletedOrders = completedTabList;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                todays_orders_pending.setText("New\n"+finalNewOrders);
//                todays_orders_completed.setText("Completed\n"+finalCompletedOrders);
//                todays_orders_total.setText("Total\n"+(finalNewOrders + finalCompletedOrders));

            }
        });

    }

    @Override
    public void onError(String calledApi, String errorMessage) {
        AlertDialogUtils.getInstance().hideLoading();
    }

    @Override
    public void onError(String calledApi, String errorMessage, int errorCode) {
        if(errorCode == 403)
            AlertDialogUtils.showAlertDialog(RiderDashboardActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), errorMessage, getString(R.string.ok), null, true);

    }
}
