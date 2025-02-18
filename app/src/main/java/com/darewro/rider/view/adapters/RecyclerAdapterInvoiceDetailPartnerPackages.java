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
import com.darewro.rider.view.models.InvoicePackage;
import com.darewro.rider.view.utils.AppUtils;

import java.util.List;

public class RecyclerAdapterInvoiceDetailPartnerPackages extends RecyclerView.Adapter<RecyclerAdapterInvoiceDetailPartnerPackages.ViewHolder> {
    Activity appCompatActivity;
    List<InvoicePackage> packagesList;
    int layoutResourceId = R.layout.recycler_item_invoice_details_partner_packages;

    public RecyclerAdapterInvoiceDetailPartnerPackages(Activity appCompatActivity, List<InvoicePackage> packages) {
        this.appCompatActivity = appCompatActivity;
        this.packagesList = packages;
    }

    @NonNull
    @Override
    public RecyclerAdapterInvoiceDetailPartnerPackages.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(appCompatActivity).inflate(layoutResourceId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterInvoiceDetailPartnerPackages.ViewHolder viewHolder, int position) {
        final InvoicePackage packages = this.packagesList.get(position);

        viewHolder.name.setText(packages.getPackageName());
        viewHolder.price.setText(packages.getPrice());
        viewHolder.quantity.setText(" (x"+packages.getQuantity()+")");

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

            AppUtils.setMontserrat(name);
            AppUtils.setMontserrat(price);
            AppUtils.setMontserrat(quantity);
        }
    }
}
