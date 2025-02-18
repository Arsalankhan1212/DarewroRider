package com.darewro.rider.view.adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.darewro.rider.R;
import com.darewro.rider.data.api.models.OrderComponent;
import com.darewro.rider.view.utils.AppUtils;

import java.util.List;

public class RecyclerAdapterInvoiceDetailGeneral extends RecyclerView.Adapter<RecyclerAdapterInvoiceDetailGeneral.ViewHolder> {
    Activity appCompatActivity;
    List<OrderComponent> packagesList;
    int layoutResourceId = R.layout.recycler_item_invoice_details_general;

    public RecyclerAdapterInvoiceDetailGeneral(Activity appCompatActivity, List<OrderComponent> packages) {
        this.appCompatActivity = appCompatActivity;
        this.packagesList = packages;
    }

    @NonNull
    @Override
    public RecyclerAdapterInvoiceDetailGeneral.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(appCompatActivity).inflate(layoutResourceId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterInvoiceDetailGeneral.ViewHolder viewHolder, int position) {
        final OrderComponent orderComponent = this.packagesList.get(position);

        viewHolder.name.setText(orderComponent.getPackages().get(0).getItem().getName());
        //viewHolder.price.setText(packages.getPrice());
        //viewHolder.quantity.setText(" (x"+packages.getQuantity()+")");

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return packagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity;
        FrameLayout card;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price_quantity);
            quantity = itemView.findViewById(R.id.total);

            AppUtils.setMontserratBold(name);
            AppUtils.setMontserratBold(price);
            AppUtils.setMontserratBold(quantity);
        }
    }
}
