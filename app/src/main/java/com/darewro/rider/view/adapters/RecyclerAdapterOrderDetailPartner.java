package com.darewro.rider.view.adapters;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.darewro.rider.R;
import com.darewro.rider.data.api.ApiCalls;
import com.darewro.rider.data.api.Interface.InitApi;
import com.darewro.rider.data.api.handlers.GenericHandler;
import com.darewro.rider.data.api.requests.JsonObjectRequestCall;
import com.darewro.rider.presenter.ResponseListenerGeneric;
import com.darewro.rider.view.activities.OrderDetailActivity;
import com.darewro.rider.view.activities.RatingActivity;
import com.darewro.rider.view.models.Order;
import com.darewro.rider.view.models.OrderPartners;
import com.darewro.rider.view.utils.AlertDialogUtils;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.SharedPreferenceHelper;

import java.util.HashMap;
import java.util.List;

public class RecyclerAdapterOrderDetailPartner extends RecyclerView.Adapter<RecyclerAdapterOrderDetailPartner.ViewHolder> implements ResponseListenerGeneric {
    Activity appCompatActivity;
    List<OrderPartners> orderPartners;
    int layoutResourceId = R.layout.recycler_item_orders_details_partner;
    Order order;
    public RecyclerAdapterOrderDetailPartner(Activity appCompatActivity, Order order, List<OrderPartners> orderPartners) {
        this.appCompatActivity = appCompatActivity;
        this.orderPartners = orderPartners;
        this.order = order;
    }

    @NonNull
    @Override
    public RecyclerAdapterOrderDetailPartner.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(appCompatActivity).inflate(layoutResourceId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterOrderDetailPartner.ViewHolder viewHolder, int position) {
        final OrderPartners orderPartner = this.orderPartners.get(position);

        viewHolder.name.setText(orderPartner.getName());
        viewHolder.total.setText(": Rs. "+String.valueOf((int)Math.ceil(Double.parseDouble(orderPartner.getPrice()))));

        if(orderPartner.getDiscount()!=null){
//            if(orderPartner.getSubTotal()!=null)
//                viewHolder.discount.setText("Rs. "+(int)Math.ceil((Double.parseDouble(orderPartner.getPrice())-Double.parseDouble(orderPartner.getSubTotal()))));
//            else {
               // int discount = ((int) Math.ceil((Double.parseDouble(orderPartner.getPrice())/100.f) * Double.parseDouble(orderPartner.getDiscount())));
                viewHolder.discount.setText("Rs. "+String.valueOf((int)Math.ceil(Double.parseDouble(orderPartner.getDiscount()))));
      //      }
        }
        else
            viewHolder.discount.setText("Rs. 0");

        if(orderPartner.getSubTotal()!=null)
            viewHolder.subamount.setText(String.valueOf((int) Math.ceil(Double.parseDouble(orderPartner.getSubTotal()))));
        else
            viewHolder.subamount.setText(String.valueOf(((int) Math.ceil(Double.parseDouble(orderPartner.getPrice()))-(int) Math.ceil(Double.parseDouble(orderPartner.getDiscount())))));


        viewHolder.location.setText(orderPartner.getDeliveryLocation());
        if(order.getOrderDetails().isSurpriseOrder()){
            viewHolder.locationContact.setText(order.getOrderDetails().getCustomer().getMsisdn());
        }else {
            if (orderPartner.getDeliveryContact() != null)
                viewHolder.locationContact.setText(orderPartner.getDeliveryContact());
        }
        viewHolder.locationContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openDialer(viewHolder.locationContact.getText().toString(),appCompatActivity);
            }
        });

        if(orderPartner.getStatus().equals(OrderPartners.PICKED))
        {
            viewHolder.chkbox.setChecked(true);
            viewHolder.chkbox.setText(OrderPartners.PICKED_TEXT);
            viewHolder.chkbox.setEnabled(false);
            viewHolder.chkbox.setClickable(false);
            //viewHolder.chkbox.setText(OrderPartners.DELIVERED_TEXT);
        }/*else if(orderPartner.getStatus().equals(OrderPartners.DELIVERED)){
            viewHolder.chkbox.setChecked(true);
            viewHolder.chkbox.setText(OrderPartners.DELIVERED_TEXT);
            viewHolder.chkbox.setEnabled(false);
            viewHolder.chkbox.setClickable(false);
        }*/else{
            viewHolder.chkbox.setChecked(false);
            viewHolder.chkbox.setText(OrderPartners.PICKED_TEXT);
        }

        if(order.getOrderDetails().getIsAccepted()==null) {
            if(order.getOrderDetails().getStatus().equals(Order.RIDER_ASSIGNED)) {
                //root_pending.setVisibility(View.VISIBLE);
                viewHolder.chkbox.setVisibility(View.INVISIBLE);

//                root_delivered.setVisibility(View.GONE);
  //              root_completed.setVisibility(View.GONE);
    //            btnDirections.setVisibility(View.INVISIBLE);
      //          btnCall.setVisibility(View.INVISIBLE);
        //        btnText.setVisibility(View.INVISIBLE);
            }else{
                viewHolder.chkbox.setVisibility(View.INVISIBLE);
/*                root_pending.setVisibility(View.GONE);
                root_delivered.setVisibility(View.GONE);
                root_completed.setVisibility(View.GONE);
                btnCall.setVisibility(View.INVISIBLE);
                btnText.setVisibility(View.INVISIBLE);
                btnDirections.setVisibility(View.INVISIBLE);*/
            }
        }else{
            if(order.getOrderDetails().getIsAccepted()!=null&&order.getOrderDetails().getStatus().equals(Order.DELIVERED)){
                viewHolder.chkbox.setVisibility(View.VISIBLE);

/*                root_pending.setVisibility(View.GONE);
                root_completed.setVisibility(View.VISIBLE);
                root_delivered.setVisibility(View.GONE);
                btnCall.setVisibility(View.VISIBLE);
                btnText.setVisibility(View.VISIBLE);
                btnDirections.setVisibility(View.VISIBLE);*/
            }else if(order.getOrderDetails().getIsAccepted()!=null&&order.getOrderDetails().getStatus().equals(Order.RIDER_ACCEPTED)){
                viewHolder.chkbox.setVisibility(View.VISIBLE);

/*                root_pending.setVisibility(View.GONE);
                root_completed.setVisibility(View.GONE);
                root_delivered.setVisibility(View.VISIBLE);
                btnCall.setVisibility(View.VISIBLE);
                btnText.setVisibility(View.VISIBLE);
                btnDirections.setVisibility(View.VISIBLE);*/
            }else{
                viewHolder.chkbox.setVisibility(View.INVISIBLE);
                /*
                root_pending.setVisibility(View.GONE);
                root_completed.setVisibility(View.GONE);
                root_delivered.setVisibility(View.GONE);
                btnCall.setVisibility(View.INVISIBLE);
                btnText.setVisibility(View.INVISIBLE);
                btnDirections.setVisibility(View.INVISIBLE);
*/
            }
        }


        viewHolder.chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    if(viewHolder.chkbox.getText().toString().equals(OrderPartners.PICKED_TEXT))
                    {
                        changeStatus(orderPartner.getId(), OrderPartners.PICKED);
                    }/*else if(viewHolder.chkbox.getText().toString().equals(OrderPartners.DELIVERED_TEXT))
                    {
                        changeStatus(orderPartner.getId(), OrderPartners.DELIVERED);
                    }*/
                }
            }
        });
        //if(orderPartner.)
       // viewHolder.chkbox.setChecked(true);

        /*viewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.collapsableView.getVisibility() == View.VISIBLE) {
                    viewHolder.collapsableView.setVisibility(View.GONE);
                    viewHolder.collapseButton.setImageResource(R.drawable.ic_arrow_up);
                } else {
                    viewHolder.collapsableView.setVisibility(View.VISIBLE);
                    viewHolder.collapseButton.setImageResource(R.drawable.ic_arrow_down);
                }
            }
        });*/

        RecyclerAdapterOrderDetailPartnerPackages adapter = new RecyclerAdapterOrderDetailPartnerPackages(appCompatActivity, orderPartner.getPackages(),order.getOrderDetails().getIsAccepted()!=null,orderPartner.getStatus().equals(OrderPartners.PICKED));
        viewHolder.recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(viewHolder.recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        viewHolder.recyclerView.addItemDecoration(dividerItemDecoration);

        viewHolder.recyclerView.setLayoutFrozen(true);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return orderPartners.size();
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
            AlertDialogUtils.showAlertDialog(appCompatActivity, AlertDialogUtils.ALERT_DIALOG_WARNING, appCompatActivity.getString(R.string.alert), appCompatActivity.getString(R.string.app_update), appCompatActivity.getString(R.string.ok), null, true);

    }

    @Override
    public void onSuccess(String calledApi, String response) {
        AppUtils.makeNotification(response,appCompatActivity);

        ((OrderDetailActivity)appCompatActivity).onRefresh();
        //notifyDataSetChanged();
        /*if(statusSwitch.isChecked()){
            SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.IS_AVAILABLE,true,appCompatActivity);
            //  SharedPreferenceHelper.saveString(SharedPreferenceHelper.RIDER_ID, SharedPreferenceHelper.getString(SharedPreferenceHelper.UID,MainActivity.this)*//*"6"*//*,MainActivity.this);
            //bindLocationService(true);
        }else{
            SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.IS_AVAILABLE,false,appCompatActivity);
//            SharedPreferenceHelper.saveString(SharedPreferenceHelper.RIDER_ID, "",MainActivity.this);
            //bindLocationService(false);
        }
*/
    }

    @Override
    public void onError(String calledApi, String errorMessage) {

    }

    @Override
    public void onError(String calledApi, String errorMessage, int errorCode) {
        if(errorCode == 403)
            AlertDialogUtils.showAlertDialog(appCompatActivity, AlertDialogUtils.ALERT_DIALOG_WARNING, appCompatActivity.getString(R.string.alert), errorMessage, appCompatActivity.getString(R.string.ok), null, true);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, locationContact, subamount, discount, total;
        FrameLayout card;
        RecyclerView recyclerView;
        LinearLayout root, collapsableView;
        ImageView collapseButton;
        CheckBox chkbox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            name = itemView.findViewById(R.id.partner_name);
            root = itemView.findViewById(R.id.root);
            chkbox = itemView.findViewById(R.id.checked);

            //collapseButton = itemView.findViewById(R.id.arrow);
            collapsableView = itemView.findViewById(R.id.collapsbale_view);
            subamount = itemView.findViewById(R.id.sub_total);
            total = itemView.findViewById(R.id.total);
            discount = itemView.findViewById(R.id.discount);
            location = itemView.findViewById(R.id.location);
            locationContact = itemView.findViewById(R.id.location_contact);

            recyclerView = itemView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(appCompatActivity, LinearLayoutManager.VERTICAL, false));
            AppUtils.setRippleAnimation(appCompatActivity, card);


            AppUtils.setMontserratBold(name);
            AppUtils.setMontserrat(subamount);
            AppUtils.setMontserrat(total);
            AppUtils.setMontserrat(discount);
            AppUtils.setMontserrat(location);
            AppUtils.setMontserrat(chkbox);
        }



    }

    private void changeStatus(final String componentId, final String status) {

        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("ComponentId", componentId);
                params.put("Status", status);
                params.put("UserID", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID,appCompatActivity));
             //   params.put("Latitude", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LAT,appCompatActivity));
             //   params.put("Longitude", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LNG,appCompatActivity));

                return params;
            }


            @Override
            public HashMap<String, Object> getObjBody() {
                return null;
            }

            @Override
            public HashMap<String, String> getHeader() {
                return AppUtils.getStandardHeaders(appCompatActivity);
            }
        };

        GenericHandler partnersHandler = new GenericHandler(appCompatActivity, ApiCalls.updateOrderComponentStatus(), this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), ApiCalls.updateOrderComponentStatus(), Request.Method.POST, appCompatActivity, partnersHandler);
        jsonObjectRequestCall.sendData();

    }
}
