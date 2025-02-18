package com.darewro.rider.view.adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.darewro.rider.R;
import com.darewro.rider.view.models.Order;
import com.darewro.rider.view.utils.AppUtils;

import java.util.List;

public class RecyclerAdapterOrderDetail extends RecyclerView.Adapter<RecyclerAdapterOrderDetail.ViewHolder> {
    Activity appCompatActivity;
    List<Order> orderList;
    int layoutResourceId = R.layout.recycler_item_order_detail;

    public RecyclerAdapterOrderDetail(Activity appCompatActivity, List<Order> orderList) {
        this.appCompatActivity = appCompatActivity;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public RecyclerAdapterOrderDetail.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(appCompatActivity).inflate(layoutResourceId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterOrderDetail.ViewHolder viewHolder, int position) {
        final Order order = this.orderList.get(position);

        viewHolder.date.setText(order.getDate());
        viewHolder.time.setText(order.getTime());
        viewHolder.status.setText(order.getStatus());

        RecyclerAdapterOrderDetailPartner adapter = new RecyclerAdapterOrderDetailPartner(appCompatActivity, order, order.getOrderPartners());
        viewHolder.recyclerView.setAdapter(adapter);
        viewHolder.recyclerView.setLayoutFrozen(true);

/*        viewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // if(order.getStatusType().equalsIgnoreCase(Order.RIDER_ACCEPTED)) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("order", order);
                    AppUtils.switchActivity(appCompatActivity, RiderTrackingActivity.class, bundle);
                //}
            }
        });*/
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
        TextView date, time, status;
        RecyclerView recyclerView;
        FrameLayout card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(appCompatActivity, LinearLayoutManager.VERTICAL, false));
            AppUtils.setRippleAnimation(appCompatActivity, card);
        }
    }
}
