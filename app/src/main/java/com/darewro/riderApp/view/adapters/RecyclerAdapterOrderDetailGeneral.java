package com.darewro.riderApp.view.adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.android.gms.maps.model.LatLng;
import com.darewro.riderApp.App;
import com.darewro.riderApp.R;
import com.darewro.riderApp.data.api.ApiCalls;
import com.darewro.riderApp.data.api.Interface.InitApi;
import com.darewro.riderApp.data.api.handlers.GenericHandler;
import com.darewro.riderApp.data.api.models.OrderComponent;
import com.darewro.riderApp.data.api.requests.JsonObjectRequestCall;
import com.darewro.riderApp.presenter.ResponseListenerGeneric;
import com.darewro.riderApp.view.activities.OrderDetailActivity;
import com.darewro.riderApp.view.activities.RatingActivity;
import com.darewro.riderApp.view.models.Order;
import com.darewro.riderApp.view.models.OrderPartners;
import com.darewro.riderApp.view.utils.AlertDialogUtils;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.LocationUtils;
import com.darewro.riderApp.view.utils.SharedPreferenceHelper;
import com.darewro.riderApp.view.utils.WeightUtils;

import java.util.HashMap;
import java.util.List;

public class RecyclerAdapterOrderDetailGeneral extends RecyclerView.Adapter<RecyclerAdapterOrderDetailGeneral.ViewHolder> implements ResponseListenerGeneric {
    Activity appCompatActivity;
    List<OrderComponent> orderPartners;
    int layoutResourceId = R.layout.recycler_item_orders_details_general;
    Order order;
    public RecyclerAdapterOrderDetailGeneral(Activity appCompatActivity, Order order, List<OrderComponent> orderPartners) {
        this.appCompatActivity = appCompatActivity;
        this.orderPartners = orderPartners;
        this.order = order;
    }

    @NonNull
    @Override
    public RecyclerAdapterOrderDetailGeneral.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(appCompatActivity).inflate(layoutResourceId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterOrderDetailGeneral.ViewHolder viewHolder, int position) {
        final OrderComponent orderComponent = this.orderPartners.get(position);

        viewHolder.name.setText(orderComponent.getPackages().get(0).getItem().getName());

        viewHolder.plocationContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openDialer(viewHolder.plocationContact.getText().toString(),appCompatActivity);
            }
        });

        viewHolder.dlocationContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openDialer(viewHolder.dlocationContact.getText().toString(),appCompatActivity);
            }
        });

        for(int i=0;i<orderComponent.getOrderLocations().size();i++)
        {
            if(orderComponent.getOrderLocations().get(i).getLocationType().equals(LocationUtils.TYPE_PICKUP))
            {
                if(orderComponent.getOrderLocations().get(i).getName()!=null){
                    viewHolder.plocation.setText(orderComponent.getOrderLocations().get(i).getName());
                    if(order.getOrderDetails().isSurpriseOrder())
                        viewHolder.dlocationContact.setText(order.getOrderDetails().getCustomer().getMsisdn());
                    else {
                        if (orderComponent.getOrderLocations().get(i).getLocationContacts() != null && orderComponent.getOrderLocations().get(i).getLocationContacts().size() > 0 && orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber() != null)
                            viewHolder.plocationContact.setText(orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber());
                    }
                }
                else {
                    LatLng latlng = new LatLng(Double.parseDouble(orderComponent.getOrderLocations().get(i).getLatitude()), Double.parseDouble(orderComponent.getOrderLocations().get(i).getLongitude()));
                    String locationName = LocationUtils.getAddress(appCompatActivity, latlng);
                    viewHolder.plocation.setText(locationName);
                    if(order.getOrderDetails().isSurpriseOrder())
                        viewHolder.dlocationContact.setText(order.getOrderDetails().getCustomer().getMsisdn());
                    else {
                        if (orderComponent.getOrderLocations().get(i).getLocationContacts() != null && orderComponent.getOrderLocations().get(i).getLocationContacts().size() > 0 && orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber() != null)
                            viewHolder.plocationContact.setText(orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber());
                    }
                }

                if (orderComponent.getOrderLocations().get(i).getManualLocation() != null && !TextUtils.isEmpty(orderComponent.getOrderLocations().get(i).getManualLocation()))
                {
                    viewHolder.orderAdditionalLocationDetail.setText(orderComponent.getOrderLocations().get(i).getManualLocation());
                    viewHolder.orderAdditionalLocationLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    viewHolder.orderAdditionalLocationLayout.setVisibility(View.GONE);
                }

            }else
            {
                if(orderComponent.getOrderLocations().get(i).getName()!=null){
                    viewHolder.dlocation.setText(orderComponent.getOrderLocations().get(i).getName());

                    if(order.getOrderDetails().isSurpriseOrder())
                        viewHolder.dlocationContact.setText(order.getOrderDetails().getCustomer().getMsisdn());
                    else {
                        if (orderComponent.getOrderLocations().get(i).getLocationContacts() != null && orderComponent.getOrderLocations().get(i).getLocationContacts().size() > 0 && orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber() != null)
                            viewHolder.dlocationContact.setText(orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber());
                    }
                }
                else {
                    LatLng latlng = new LatLng(Double.parseDouble(orderComponent.getOrderLocations().get(i).getLatitude()), Double.parseDouble(orderComponent.getOrderLocations().get(i).getLongitude()));
                    String locationName = LocationUtils.getAddress(appCompatActivity, latlng);
                    viewHolder.dlocation.setText(locationName);
                    if(order.getOrderDetails().isSurpriseOrder())
                        viewHolder.dlocationContact.setText(order.getOrderDetails().getCustomer().getMsisdn());
                    else {
                        if (orderComponent.getOrderLocations().get(i).getLocationContacts() != null && orderComponent.getOrderLocations().get(i).getLocationContacts().size() > 0 && orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber() != null)
                            viewHolder.dlocationContact.setText(orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber());
                    }
                }
                if (orderComponent.getOrderLocations().get(i).getManualLocation() != null && !TextUtils.isEmpty(orderComponent.getOrderLocations().get(i).getManualLocation()))
                {
                    viewHolder.orderAdditionalLocationDetail.setText(orderComponent.getOrderLocations().get(i).getManualLocation());
                    viewHolder.orderAdditionalLocationLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    viewHolder.orderAdditionalLocationLayout.setVisibility(View.GONE);
                }

            }
        }

        if(orderComponent.getPackages().get(0).getItem().getItemObject().isFragile())
            viewHolder.fragile.setChecked(true);
        else
            viewHolder.fragile.setChecked(false);

        viewHolder.description.setText(orderComponent.getPackages().get(0).getItem().getItemObject().getDescription());

        String amount = order.getOrderDetails().getEstimatedOrderPrice();
        if (TextUtils.isEmpty(amount) || amount == null) {
            viewHolder.price.setText("0");
        }
        else {
            viewHolder.price.setText(amount);
        }
        int progress = 0;
        if (viewHolder.price != null && !TextUtils.isEmpty(amount)) {

            int pr = Integer.parseInt(amount);

            progress = (int)((100f / WeightUtils.PRICE_MAX_LIMIT) * pr);

        }
        viewHolder.weight.setProgress(progress);

/*

        if(orderComponent.getPackages().get(0).getItem().getItemObject().getWeightCategory().equals(WeightUtils.TYPE_BELOW_ONE_KG)) {
            viewHolder.weight.setProgress(0);
            viewHolder.progress.setText(WeightUtils.TYPE_BELOW_ONE_KG_STRING);
        }if(orderComponent.getPackages().get(0).getItem().getItemObject().getWeightCategory().equals(WeightUtils.TYPE_ABOVE_ONE_KG)) {
            viewHolder.weight.setProgress(20);
            viewHolder.progress.setText(WeightUtils.TYPE_ABOVE_ONE_KG_STRING);
        }
*/

        viewHolder.weight.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
       // viewHolder.weight.setClickable(false);
        //viewHolder.weight.setEnabled(false);

        if(orderComponent.getStatus().equals(OrderPartners.PICKED))
        {
            viewHolder.chkbox.setChecked(true);
            viewHolder.chkbox.setText(OrderPartners.PICKED_TEXT);
            viewHolder.chkbox.setEnabled(false);
            viewHolder.chkbox.setClickable(false);
//            viewHolder.chkbox.setText(OrderPartners.DELIVERED_TEXT);
        }/*else if(orderComponent.getStatus().equals(OrderPartners.DELIVERED)){
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
                        changeStatus(orderComponent.getId(), OrderPartners.PICKED);
                    }/*else if(viewHolder.chkbox.getText().toString().equals(OrderPartners.DELIVERED_TEXT))
                    {
                        changeStatus(orderComponent.getId(), OrderPartners.DELIVERED);
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

       /* RecyclerAdapterOrderDetailPartnerPackages adapter = new RecyclerAdapterOrderDetailPartnerPackages(appCompatActivity, orderComponent.getPackages());
        viewHolder.recyclerView.setAdapter(adapter);
        viewHolder.recyclerView.setLayoutFrozen(true);*/
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
        TextView priceTitle, price, priceCurrency,name, plocation,dlocation, description, progress,orderAdditionalLocationDetail,plocationContact,dlocationContact;
        CheckBox fragile;
        FrameLayout card;
        //RecyclerView recyclerView;
        LinearLayout root, collapsableView,orderAdditionalLocationLayout;
        SeekBar weight;

        ImageView collapseButton;
        CheckBox chkbox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            name = itemView.findViewById(R.id.partner_name);
            root = itemView.findViewById(R.id.root);
            chkbox = itemView.findViewById(R.id.checked);
            fragile = itemView.findViewById(R.id.fragile);
            weight = itemView.findViewById(R.id.weight);
            progress = itemView.findViewById(R.id.progress);


            orderAdditionalLocationDetail = itemView.findViewById(R.id.location_additional);
            orderAdditionalLocationLayout = itemView.findViewById(R.id.location_additional_layout);

            //collapseButton = itemView.findViewById(R.id.arrow);
            collapsableView = itemView.findViewById(R.id.collapsbale_view);
            priceTitle = itemView.findViewById(R.id.price_title);
            priceCurrency = itemView.findViewById(R.id.price_currency);
            price = itemView.findViewById(R.id.price);
            plocation = itemView.findViewById(R.id.plocation);
            dlocation = itemView.findViewById(R.id.dlocation);
            plocationContact = itemView.findViewById(R.id.plocation_contact);
            dlocationContact = itemView.findViewById(R.id.dlocation_contact);
            description = itemView.findViewById(R.id.description);

            AppUtils.setMontserratBold(name);
            AppUtils.setMontserrat(priceTitle);
            AppUtils.setMontserrat(priceCurrency);
            AppUtils.setMontserratBold(price);
            AppUtils.setMontserrat((TextView)itemView.findViewById(R.id.zero));
            AppUtils.setMontserrat((TextView)itemView.findViewById(R.id.twenty));
            AppUtils.setMontserratBold((TextView)itemView.findViewById(R.id.plocation_label));
            AppUtils.setMontserratBold((TextView)itemView.findViewById(R.id.
                    location_label_additional));
            AppUtils.setMontserrat(fragile);
            AppUtils.setMontserrat(plocation);
            AppUtils.setMontserrat(orderAdditionalLocationDetail);


          //  recyclerView = itemView.findViewById(R.id.recyclerView);
          //  recyclerView.setLayoutManager(new LinearLayoutManager(appCompatActivity, LinearLayoutManager.VERTICAL, false));
            AppUtils.setRippleAnimation(appCompatActivity, card);}
    }

    private void changeStatus(final String componentId, final String status) {

        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("ComponentId", componentId);
                params.put("Status", status);
                params.put("UserID", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID,appCompatActivity));
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
