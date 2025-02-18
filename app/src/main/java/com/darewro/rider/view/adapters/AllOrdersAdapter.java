package com.darewro.rider.view.adapters;

import static com.darewro.rider.view.models.Order.NEW_TEXT;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.darewro.rider.R;
import com.darewro.rider.data.api.models.allOrders.AllOrder;
import com.darewro.rider.data.api.models.allOrders.Customer;
import com.darewro.rider.data.api.models.allOrders.LocationContact;
import com.darewro.rider.data.api.models.allOrders.OrderLocation;
import com.darewro.rider.view.activities.OrderDetailActivity;
import com.darewro.rider.view.models.Order;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.LocationUtils;
import com.darewro.rider.view.utils.SharedPreferenceHelper;
import com.darewro.rider.view.utils.TimeAgo;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;
import java.util.Objects;

public class AllOrdersAdapter extends RecyclerView.Adapter<AllOrdersAdapter.ViewHolder> {
    Activity appCompatActivity;
    List<AllOrder> orderList;
    int layoutResourceId = R.layout.layout_order_item;

    public AllOrdersAdapter(Activity appCompatActivity, List<AllOrder> orderList) {
        this.appCompatActivity = appCompatActivity;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public AllOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(appCompatActivity).inflate(layoutResourceId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AllOrdersAdapter.ViewHolder viewHolder, int position) {
        final AllOrder order = this.orderList.get(position);

        if (Objects.equals(order.getStatus(), NEW_TEXT)){
            Customer customer = order.getCustomer();
            viewHolder.tvUserName.setText(customer.getUserName());
            viewHolder.tvUserRating.setText("5("+customer.getRating() +")");
            Glide.with(appCompatActivity.getApplicationContext())
                    .load(customer.getPicturePath())
                    .centerCrop()
                    .placeholder(R.drawable.ic_profile_pic)
                    .into(viewHolder.profileImage);


            viewHolder.tvTimeAgo.setText(TimeAgo.getTimeAgo(order.getCreated()));
            List<OrderLocation> orderLocationList = order.getOrderComponents().get(0).getOrderLocations();
            viewHolder.tvDropLocation.setText(orderLocationList.get(0).getLocationName());
            viewHolder.tvPickUpLocation.setText(orderLocationList.get(1).getLocationName());


            viewHolder.tvPrice.setText("PKR 342");
            //viewHolder.tvDistance.setText(AppUtils.calculateDistanceInKilometer());

            viewHolder.tvOrderType.setText(order.getOrderType().toUpperCase());

            if(order.getOrderType().equalsIgnoreCase(Order.ORDER_TYPE_GENERAL_TEXT))
                viewHolder.imgOrderType.setImageResource(R.drawable.general_delivery_icon);
            else
                viewHolder.imgOrderType.setImageResource(R.drawable.food_delivery_icon);


            viewHolder.layoutCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String orderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, appCompatActivity);

                    if (orderId.equals("")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("orderId", String.valueOf(order.getId()));
                        bundle.putBoolean("fromAllOrderList",true);
                        AppUtils.switchActivity(appCompatActivity, OrderDetailActivity.class, bundle);
                    }
                    else{
                        Toast.makeText(appCompatActivity,appCompatActivity.getString(R.string.str_running_order_msg),Toast.LENGTH_LONG).show();
                    }

                }
            });

        }
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
        ImageView imgOrderType;
        ShapeableImageView profileImage;
        TextView tvUserName, tvUserRating, tvTimeAgo, tvPickUpLocation,tvDropLocation, tvPrice,
                tvDistance, tvOrderType;

        ConstraintLayout layoutCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.user_name);
            tvUserRating = itemView.findViewById(R.id.user_rating);
            tvTimeAgo = itemView.findViewById(R.id.time_ago);
            tvPickUpLocation = itemView.findViewById(R.id.tvPickUpLocation);
            tvDropLocation = itemView.findViewById(R.id.tvDropLocation);
            tvPrice = itemView.findViewById(R.id.price);
            tvDistance = itemView.findViewById(R.id.distance);
            tvOrderType = itemView.findViewById(R.id.tvOrderType);
            imgOrderType = itemView.findViewById(R.id.imgOrderType);
            layoutCard = itemView.findViewById(R.id.layoutCard);



            profileImage = itemView.findViewById(R.id.profile_icon);

            AppUtils.setRippleAnimation(appCompatActivity, layoutCard);

        }
    }
}
