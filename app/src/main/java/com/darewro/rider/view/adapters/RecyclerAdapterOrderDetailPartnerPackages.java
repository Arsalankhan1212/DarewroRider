package com.darewro.rider.view.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.darewro.rider.R;
import com.darewro.rider.data.api.models.Packages;
import com.darewro.rider.view.models.OrderPartners;
import com.darewro.rider.view.utils.AppUtils;

import java.util.List;

public class RecyclerAdapterOrderDetailPartnerPackages extends RecyclerView.Adapter<RecyclerAdapterOrderDetailPartnerPackages.ViewHolder> {
    Activity appCompatActivity;
    List<Packages> packagesList;
    int layoutResourceId = R.layout.recycler_item_orders_details_partner_packages;
    boolean orderAccepted = false;
    boolean picked = false;

    public RecyclerAdapterOrderDetailPartnerPackages(Activity appCompatActivity, List<Packages> packages, boolean orderAccepted, boolean picked) {
        this.appCompatActivity = appCompatActivity;
        this.packagesList = packages;
        this.orderAccepted = orderAccepted;
        this.picked = picked;
    }

    @NonNull
    @Override
    public RecyclerAdapterOrderDetailPartnerPackages.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(appCompatActivity).inflate(layoutResourceId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterOrderDetailPartnerPackages.ViewHolder viewHolder, int position) {
        final Packages packages = this.packagesList.get(position);

        viewHolder.name.setText(packages.getPackageName());

        if(packages.getDiscount()!=null){
            int discount = ((int) Math.ceil((Double.parseDouble(packages.getPrice())/100.f) * Double.parseDouble(packages.getDiscount())));
            viewHolder.discount.setText("Rs. "+discount+ " @ " +String.valueOf((int) Math.ceil(Double.parseDouble(packages.getDiscount()))) + " %");
        }
        else
            viewHolder.discount.setText("Rs. 0 @ 0 %");

        if(packages.getPrice()!=null)
            viewHolder.sub_total.setText(String.valueOf((int) Math.ceil(Double.parseDouble(packages.getPrice()))));
        else
            viewHolder.sub_total.setText(String.valueOf((int) Math.ceil(Double.parseDouble(packages.getPrice()))));

        if(packages.getDiscount().equals("0"))
        viewHolder.total.setText(": Rs. "+String.valueOf((int)Math.ceil(Double.parseDouble(packages.getPrice()))));
        else{
            int discount = ((int) Math.ceil((Double.parseDouble(packages.getPrice())/100.f) * Double.parseDouble(packages.getDiscount())));
            viewHolder.total.setText(": Rs. "+String.valueOf(((int)Math.ceil(Double.parseDouble(packages.getPrice()))-discount)));
        }




        viewHolder.quantity.setText(" (x" + packages.getQuantity() + ")");

        if (orderAccepted) {
            if(picked)
            {
                viewHolder.chkbox.setChecked(true);
                viewHolder.chkbox.setText(OrderPartners.PICKED_TEXT);
                viewHolder.chkbox.setEnabled(false);
                viewHolder.chkbox.setClickable(false);
            }
            viewHolder.chkbox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.chkbox.setVisibility(View.INVISIBLE);
        }

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
        CheckBox chkbox;
        TextView name, quantity, total, discount, sub_total;
        FrameLayout card;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            name = itemView.findViewById(R.id.name);
            total = itemView.findViewById(R.id.total);
            discount = itemView.findViewById(R.id.discount);
            sub_total = itemView.findViewById(R.id.sub_total);

            quantity = itemView.findViewById(R.id.quantity);
            chkbox = itemView.findViewById(R.id.checked);

            AppUtils.setMontserrat(name);
            AppUtils.setMontserrat(total);
            AppUtils.setMontserrat(discount);
            AppUtils.setMontserrat(sub_total);
            AppUtils.setMontserrat(quantity);
            AppUtils.setMontserrat(chkbox);
        }
    }
}
