package com.darewro.riderApp.view.activities;

import android.app.Activity;
import android.os.Bundle;

import com.darewro.riderApp.R;
import com.darewro.riderApp.view.listeners.AlertDialogResponseListener;
import com.darewro.riderApp.view.utils.AlertDialogUtils;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.DbUtils;

public class DialogActivity extends Activity implements AlertDialogResponseListener
{

    String title, message, orderId,eventType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getExtras()!=null){
            title = getIntent().getStringExtra("title");
            message = getIntent().getStringExtra("message");
            eventType = getIntent().getStringExtra("eventType");
            orderId = getIntent().getStringExtra("orderId");
        }
        if(eventType.equalsIgnoreCase("OrderCancelledtoRiderEvent")){
            DbUtils.deleteOrderData(orderId);
            AlertDialogUtils.showAlertDialog(DialogActivity.this, AlertDialogUtils.ALERT_DIALOG_ACCEPT_REJECT, title, message, getString(R.string.details), DialogActivity.this);
        }else if(eventType.equalsIgnoreCase("OrderReAssignedToRiderEvent")){
            DbUtils.deleteOrderData(orderId);
            AlertDialogUtils.showAlertDialog(DialogActivity.this, AlertDialogUtils.ALERT_DIALOG_ACCEPT_REJECT, title, message, getString(R.string.details), DialogActivity.this);
        }
        else {
            if (message != null && message.equals("Force_Logout")) {
                AppUtils.forceLogout(DialogActivity.this);
            }
//            else if(eventType.equalsIgnoreCase("AssignedOrderRejectionPushEvent")) {
//                AlertDialogUtils.showAlertDialog(DialogActivity.this, AlertDialogUtils.ALERT_DIALOG_ACCEPT_REJECT, title, message, getString(R.string.details), DialogActivity.this);
//            }
            else if(eventType.equalsIgnoreCase("AssignedOrderAcceptanceBenefitPushEvent")){
                AlertDialogUtils.showAlertDialog(DialogActivity.this, AlertDialogUtils.ALERT_DIALOG_ACCEPT_REJECT, title, message, getString(R.string.details), DialogActivity.this);
            }
            else {
                AlertDialogUtils.showAlertDialog(DialogActivity.this, AlertDialogUtils.ALERT_DIALOG_ACCEPT_REJECT, title, message, getString(R.string.details), DialogActivity.this);
            }
        }
    }

    @Override
    public void OnSuccess() {

    }

    @Override
    public void OnCancel() {

    }

    @Override
    public void OnSuccess(Object object) {

    }

    @Override
    public void OnCancel(Object object) {

    }

    @Override
    public void OnSuccess(int alertId) {

        launchActivity();
    }

    @Override
    public void OnCancel(int alertId) {
       // stopService();
    }

    @Override
    public void OnSuccess(int alertId, Object object) {

    }

    @Override
    public void OnCancel(int alertId, Object object) {

    }

    @Override
    public void OnSuccess(int alertId, Object object, Object object2) {

    }

    @Override
    public void OnCancel(int alertId, Object object, Object object2) {

    }

    private void launchActivity() {

        if(orderId==null){
            AppUtils.switchActivity(DialogActivity.this,NotificationsActivity.class,null);
        }
        else{
            if(eventType.equalsIgnoreCase("OrderReAssignedToRiderEvent")||eventType.equalsIgnoreCase("OrderCancelledtoRiderEvent")){

            }
        else {
                Bundle bundle = new Bundle();
                bundle.putString("orderId", orderId);
                AppUtils.switchActivity(DialogActivity.this, OrderDetailActivity.class, bundle);
            } }
        DialogActivity.this.finish();

    }


}
