package com.darewro.riderApp.view.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.darewro.riderApp.R;
import com.darewro.riderApp.view.activities.OrderDetailActivity;
import com.darewro.riderApp.view.models.Order;
import com.darewro.riderApp.view.utils.AlertDialogUtils;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.LocationUtils;

import java.util.List;

public class RecyclerAdapterOrders extends RecyclerView.Adapter<RecyclerAdapterOrders.ViewHolder> {
    Activity appCompatActivity;
    List<Order> orderList;
    int layoutResourceId = R.layout.recycler_item_orders;

    public RecyclerAdapterOrders(Activity appCompatActivity, List<Order> orderList) {
        this.appCompatActivity = appCompatActivity;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public RecyclerAdapterOrders.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(appCompatActivity).inflate(layoutResourceId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterOrders.ViewHolder viewHolder, int position) {
        final Order order = this.orderList.get(position);

        viewHolder.date.setText(order.getDate());
        viewHolder.time.setText(order.getTime());
        viewHolder.status.setText(order.getStatus());
        viewHolder.refId.setText("Ref # "+order.getOrderDetails().getReferenceNumber());

        viewHolder.estimatedDeliveryFee.setText(order.getOrderDetails().getEstimatedDeliveryFee() +" Rs");

        if(order.getOrderDetails().getPaymentLocation()!=null&&order.getOrderDetails().getPaymentLocation().getName()!=null) {
            viewHolder.dLocation.setText(order.getOrderDetails().getPaymentLocation().getName());
        }
        else
        {
            if(order.getOrderDetails().getPaymentLocation()!=null&&order.getOrderDetails().getPaymentLocation().getLatitude()!=null&&(!order.getOrderDetails().getPaymentLocation().getLatitude().equals(""))&&order.getOrderDetails().getPaymentLocation().getLongitude()!=null&&(!order.getOrderDetails().getPaymentLocation().getLongitude().equals(""))) {
                LatLng latlng = new LatLng(Double.parseDouble(order.getOrderDetails().getPaymentLocation().getLatitude()), Double.parseDouble(order.getOrderDetails().getPaymentLocation().getLongitude()));
                String locationName = LocationUtils.getAddress(appCompatActivity, latlng);
                //if(locationName!=null&&(!locationName.equals("")))
                viewHolder.dLocation.setText(locationName);
                /*else
                {

                }*/
            }
        }

        if (order.getOrderDetails().getOrderAdditionalComment() != null && !TextUtils.isEmpty(order.getOrderDetails().getOrderAdditionalComment())) {
            viewHolder.addOnsLayout.setVisibility(View.VISIBLE);
            viewHolder.addOns.setText(order.getOrderDetails().getOrderAdditionalComment());

//            viewHolder.addOns.setOnClickListener(new View.OnClickListener() {
//
//                public void onClick(View arg0) {
//
//                    AlertDialogUtils.showAlert(order.getOrderDetails().getOrderAdditionalComment(), appCompatActivity);
//
//                }
//            });
        } else {
            viewHolder.addOnsLayout.setVisibility(View.GONE);
        }
        if(order.getOrderDetails().getOrderType().equals(Order.ORDER_TYPE_PARTNER)) {
            RecyclerAdapterOrderPartners adapter = new RecyclerAdapterOrderPartners(appCompatActivity, order, order.getOrderPartners());
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(viewHolder.recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            viewHolder.recyclerView.addItemDecoration(dividerItemDecoration);
            viewHolder.recyclerView.setAdapter(adapter);
            viewHolder.recyclerView.setLayoutFrozen(true);
        }else{
            RecyclerAdapterOrderGeneral adapter = new RecyclerAdapterOrderGeneral(appCompatActivity, order, order.getOrderDetails().getOrderComponents());
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(viewHolder.recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            viewHolder.recyclerView.addItemDecoration(dividerItemDecoration);
            viewHolder.recyclerView.setAdapter(adapter);
            viewHolder.recyclerView.setLayoutFrozen(true);
        }

        if(order.getOrderDetails().getOrderType().equals(Order.ORDER_TYPE_GENERAL))
            viewHolder.orderType.setImageResource(R.drawable.general_delivery_icon);
        else
            viewHolder.orderType.setImageResource(R.drawable.food_delivery_icon);
        viewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // if(order.getStatusType().equalsIgnoreCase(Order.RIDER_ACCEPTED)) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("order", order);
                    AppUtils.switchActivity(appCompatActivity, OrderDetailActivity.class, bundle);
                //}
            }
        });

        viewHolder.created_by.setText(order.getOrderDetails().getCreatedByName());

        if(order.getOrderDetails().isCorporateCustomers())
            viewHolder.is_corporate.setVisibility(View.VISIBLE);
        else
            viewHolder.is_corporate.setVisibility(View.GONE);

        if(order.getOrderDetails().isTwoWayOrder())
            viewHolder.two_way.setVisibility(View.VISIBLE);
        else
            viewHolder.two_way.setVisibility(View.GONE);

        if(order.getOrderDetails().isFrequentCustomer())
            viewHolder.isFrequent.setVisibility(View.VISIBLE);
        else
            viewHolder.isFrequent.setVisibility(View.GONE);

        if(order.getOrderDetails().isDuplicateOrder())
            viewHolder.isDuplicate.setVisibility(View.VISIBLE);
        else
            viewHolder.isDuplicate.setVisibility(View.GONE);

        if(order.getOrderDetails().isOnlinePayment())
            viewHolder.isOnlinePayment.setVisibility(View.VISIBLE);
        else
            viewHolder.isOnlinePayment.setVisibility(View.GONE);

        if(order.getOrderDetails().isReserved())
            viewHolder.isReserved.setVisibility(View.VISIBLE);
        else
            viewHolder.isReserved.setVisibility(View.GONE);

        if(order.getOrderDetails().isMoreThan3000())
            viewHolder.more_than_3000.setVisibility(View.VISIBLE);
        else
            viewHolder.more_than_3000.setVisibility(View.GONE);

        if(order.getOrderDetails().isEdited())
            viewHolder.is_edited.setVisibility(View.VISIBLE);
        else
            viewHolder.is_edited.setVisibility(View.GONE);

        if(order.getOrderDetails().isFragile())
            viewHolder.is_fragile.setVisibility(View.VISIBLE);
        else
            viewHolder.is_fragile.setVisibility(View.GONE);

        if(order.getOrderDetails().isMultiplePickup())
            viewHolder.multi_picked.setVisibility(View.VISIBLE);
        else
            viewHolder.multi_picked.setVisibility(View.GONE);

        if(order.getOrderDetails().isMultipleDelivery())
            viewHolder.multi_delivered.setVisibility(View.VISIBLE);
        else
            viewHolder.multi_delivered.setVisibility(View.GONE);


        if(order.getOrderDetails().isSurpriseOrder())
            viewHolder.is_surprise.setVisibility(View.VISIBLE);
        else
            viewHolder.is_surprise.setVisibility(View.GONE);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date, time, status, refId, dLocation,addOnsTitle,addOns,estimatedDeliveryFeeTitle,estimatedDeliveryFee;
        LinearLayout addOnsLayout;
        RecyclerView recyclerView;
        CardView card;
        ImageView orderType;
        TextView is_corporate, two_way, isFrequent, isDuplicate, isOnlinePayment, isReserved,more_than_3000, is_edited, is_fragile, multi_picked, multi_delivered,is_surprise, created_by;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            refId = itemView.findViewById(R.id.refId);
            addOnsTitle = itemView.findViewById(R.id.add_ons_title);
            addOnsLayout = itemView.findViewById(R.id.add_ons_layout);
            addOns = itemView.findViewById(R.id.add_ons);
            estimatedDeliveryFeeTitle = itemView.findViewById(R.id.estimated_delivery_fee_title);
            estimatedDeliveryFee = itemView.findViewById(R.id.estimated_delivery_fee);
            dLocation = itemView.findViewById(R.id.dlocation);
            orderType = itemView.findViewById(R.id.orderType);
            recyclerView = itemView.findViewById(R.id.recyclerView);

            is_corporate = itemView.findViewById(R.id.is_corporate);
            two_way = itemView.findViewById(R.id.two_way);
            isFrequent = itemView.findViewById(R.id.isFrequent);
            isDuplicate = itemView.findViewById(R.id.isDuplicate);
            isOnlinePayment = itemView.findViewById(R.id.isOnlinePayment);
            isReserved = itemView.findViewById(R.id.isReserved);
            more_than_3000 = itemView.findViewById(R.id.more_than_3000);
            is_edited = itemView.findViewById(R.id.is_edited);
            is_fragile = itemView.findViewById(R.id.is_fragile);
            multi_picked = itemView.findViewById(R.id.multi_picked);
            multi_delivered = itemView.findViewById(R.id.multi_delivered);
            is_surprise = itemView.findViewById(R.id.is_surprise);
            created_by = itemView.findViewById(R.id.created_by);

            recyclerView.setLayoutManager(new LinearLayoutManager(appCompatActivity, recyclerView.VERTICAL, false));
            AppUtils.setRippleAnimation(appCompatActivity, card);

            AppUtils.setMontserratBold(date);
            AppUtils.setMontserratBold(time);
            AppUtils.setMontserratBold(status);
            AppUtils.setMontserratBold(refId);
            AppUtils.setMontserrat(addOnsTitle);
            AppUtils.setMontserrat(addOns);
            AppUtils.setMontserrat(estimatedDeliveryFee);
            AppUtils.setMontserrat(estimatedDeliveryFeeTitle);
            AppUtils.setMontserrat(dLocation);
            AppUtils.setMontserrat(orderType);
            AppUtils.setMontserrat(created_by);
        }
    }
}
