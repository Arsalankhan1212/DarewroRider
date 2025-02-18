package com.darewro.rider.view.adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.darewro.rider.R;
import com.darewro.rider.view.models.InvoicePartner;
import com.darewro.rider.view.utils.AppUtils;

import java.util.List;

public class RecyclerAdapterInvoiceDetailPartner extends RecyclerView.Adapter<RecyclerAdapterInvoiceDetailPartner.ViewHolder> {
    Activity appCompatActivity;
    List<InvoicePartner> invoicePartner;
    int layoutResourceId = R.layout.recycler_item_invoice_details_partner;

    public RecyclerAdapterInvoiceDetailPartner(Activity appCompatActivity, List<InvoicePartner> orderPartners) {
        this.appCompatActivity = appCompatActivity;
        this.invoicePartner = orderPartners;
    }

    @NonNull
    @Override
    public RecyclerAdapterInvoiceDetailPartner.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(appCompatActivity).inflate(layoutResourceId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterInvoiceDetailPartner.ViewHolder viewHolder, int position) {
        final InvoicePartner invoicePartner = this.invoicePartner.get(position);

        viewHolder.name.setText(invoicePartner.getPartnerName());
        viewHolder.price.setText("Rs. "+invoicePartner.getTotal());


        RecyclerAdapterInvoiceDetailPartnerPackages adapter = new RecyclerAdapterInvoiceDetailPartnerPackages(appCompatActivity, invoicePartner.getInvoicePackages());
        viewHolder.recyclerView.setAdapter(adapter);
        viewHolder.recyclerView.setLayoutFrozen(true);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return invoicePartner.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        FrameLayout card;
        RecyclerView recyclerView;
        LinearLayout root, collapsableView;
        ImageView collapseButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            name = itemView.findViewById(R.id.partner_name);
            root = itemView.findViewById(R.id.root);
           // collapseButton = itemView.findViewById(R.id.arrow);
            collapsableView = itemView.findViewById(R.id.collapsbale_view);
            price = itemView.findViewById(R.id.price);
           // location = itemView.findViewById(R.id.location);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(appCompatActivity, LinearLayoutManager.VERTICAL, false));
            AppUtils.setRippleAnimation(appCompatActivity, card);


            AppUtils.setMontserratBold(name);
            AppUtils.setMontserratBold(price);
        }

    }
}
