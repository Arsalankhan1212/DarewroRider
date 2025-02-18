package com.darewro.rider.view.adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darewro.rider.R;
import com.darewro.rider.view.models.Order;
import com.darewro.rider.view.models.OrderPartners;
import com.darewro.rider.view.utils.AppUtils;

import java.util.List;

public class RecyclerAdapterOrderPartners extends RecyclerView.Adapter<RecyclerAdapterOrderPartners.ViewHolder> {
    Activity appCompatActivity;
    List<OrderPartners> orderPartners;
    int layoutResourceId = R.layout.recycler_item_orders_partners;
    Order order;
    public RecyclerAdapterOrderPartners(Activity appCompatActivity, Order order, List<OrderPartners> orderPartners) {
        this.appCompatActivity = appCompatActivity;
        this.orderPartners = orderPartners;
        this.order = order;
    }

    @NonNull
    @Override
    public RecyclerAdapterOrderPartners.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(appCompatActivity).inflate(layoutResourceId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterOrderPartners.ViewHolder viewHolder, int position) {
        final OrderPartners orderPartner = this.orderPartners.get(position);

        //if(order.getOrderDetails().getOrderType()==Order.ORDER_TYPE_PARTNER) {
            viewHolder.name.setText(orderPartner.getName());
            viewHolder.total.setText(String.valueOf((int) Math.ceil(Double.parseDouble(orderPartner.getPrice()))));
            if(orderPartner.getDiscount()!=null) {

                if(orderPartner.getSubTotal()!=null)
                viewHolder.discount.setText("Rs. "+(Double.parseDouble(orderPartner.getPrice())-Double.parseDouble(orderPartner.getSubTotal()))+ " @ "+String.valueOf((int) Math.ceil(Double.parseDouble(orderPartner.getDiscount()))) + " %");
                else {
                    int discount = ((int) Math.ceil((Double.parseDouble(orderPartner.getPrice())/100.f) * Double.parseDouble(orderPartner.getDiscount())));
                    viewHolder.discount.setText("Rs. "+discount+ " @ " +String.valueOf((int) Math.ceil(Double.parseDouble(orderPartner.getDiscount()))) + " %");
                }
            }else
                viewHolder.discount.setText("Rs. 0 @ 0 %");

            if(orderPartner.getSubTotal()!=null)
            viewHolder.subAmount.setText(String.valueOf((int) Math.ceil(Double.parseDouble(orderPartner.getSubTotal()))));
            else
                viewHolder.subAmount.setText(String.valueOf((int) Math.ceil(Double.parseDouble(orderPartner.getPrice()))));

        viewHolder.location.setText(orderPartner.getDeliveryLocation());
       /* }else{
            viewHolder.name.setText(orderPartner.getPackages().get(0).getItem().getName());
            viewHolder.price.setText("");
            viewHolder.location.setText(orderPartner.getDeliveryLocation());
        }*/

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return orderPartners.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, subAmount, discount, total;
        CardView card;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            name = itemView.findViewById(R.id.partner_name);
            total = itemView.findViewById(R.id.total);
            subAmount = itemView.findViewById(R.id.sub_total);
            discount = itemView.findViewById(R.id.discount);

            location = itemView.findViewById(R.id.location);

            AppUtils.setMontserrat(name);
            AppUtils.setMontserrat(total);
            AppUtils.setMontserrat(subAmount);
            AppUtils.setMontserrat(discount);
            AppUtils.setMontserrat(location);
        }
    }
}
