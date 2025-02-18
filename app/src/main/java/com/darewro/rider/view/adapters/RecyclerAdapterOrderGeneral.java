package com.darewro.rider.view.adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.darewro.rider.R;
import com.darewro.rider.data.api.models.OrderComponent;
import com.darewro.rider.view.models.Order;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.LocationUtils;

import java.util.List;

public class RecyclerAdapterOrderGeneral extends RecyclerView.Adapter<RecyclerAdapterOrderGeneral.ViewHolder> {
    Activity appCompatActivity;
    List<OrderComponent> orderComponents;
    int layoutResourceId = R.layout.recycler_item_orders_general;
    Order order;
    public RecyclerAdapterOrderGeneral(Activity appCompatActivity, Order order, List<OrderComponent> orderComponents) {
        this.appCompatActivity = appCompatActivity;
        this.orderComponents = orderComponents;
        this.order = order;
    }

    @NonNull
    @Override
    public RecyclerAdapterOrderGeneral.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(appCompatActivity).inflate(layoutResourceId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterOrderGeneral.ViewHolder viewHolder, int position) {
        final OrderComponent orderComponent = this.orderComponents.get(position);



        viewHolder.name.setText(orderComponent.getPackages().get(0).getItem().getName());
        // viewHolder.price.setText("");

        for(int i=0;i<orderComponent.getOrderLocations().size();i++)
        {
            if(orderComponent.getOrderLocations().get(i).getLocationType().equals(LocationUtils.TYPE_PICKUP))
            {
                if(orderComponent.getOrderLocations().get(i).getName()!=null){
                    viewHolder.plocation.setText(orderComponent.getOrderLocations().get(i).getName());
                    //if(orderComponent.getOrderLocations().get(i).getLocationContacts()!=null&&orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber()!=null)
                      //  viewHolder.contact.setText(orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber());
                }
                else {
                    LatLng latlng = new LatLng(Double.parseDouble(orderComponent.getOrderLocations().get(i).getLatitude()), Double.parseDouble(orderComponent.getOrderLocations().get(i).getLongitude()));
                    String locationName = LocationUtils.getAddress(appCompatActivity, latlng);
                    viewHolder.plocation.setText(locationName);
                    //if(orderComponent.getOrderLocations().get(i).getLocationContacts()!=null&&orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber()!=null)
                      //  viewHolder.contact.setText(orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber());
                }
            }else
            {
                if(orderComponent.getOrderLocations().get(i).getName()!=null){
                    viewHolder.dlocation.setText(orderComponent.getOrderLocations().get(i).getName());
                    //if(orderComponent.getOrderLocations().get(i).getLocationContacts()!=null&&orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber()!=null)
                      //  viewHolder.contact.setText(orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber());
                }
                else {
                    LatLng latlng = new LatLng(Double.parseDouble(orderComponent.getOrderLocations().get(i).getLatitude()), Double.parseDouble(orderComponent.getOrderLocations().get(i).getLongitude()));
                    String locationName = LocationUtils.getAddress(appCompatActivity, latlng);
                    viewHolder.dlocation.setText(locationName);
                    //if(orderComponent.getOrderLocations().get(i).getLocationContacts()!=null&&orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber()!=null)
                      //  viewHolder.contact.setText(orderComponent.getOrderLocations().get(i).getLocationContacts().get(0).getContactNumber());
                }
            }
        }
          /*  viewHolder.name.setText(orderPartner.getPackages().get());
            viewHolder.price.setText(String.valueOf((int) Math.ceil(Double.parseDouble(orderPartner.getPrice()))));
            viewHolder.location.setText(orderPartner.getDeliveryLocation());
*/
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return orderComponents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, plocation, dlocation;//,contact;
        CardView card;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            name = itemView.findViewById(R.id.partner_name);
            //price = itemView.findViewById(R.id.price);
            plocation = itemView.findViewById(R.id.plocation);
            dlocation = itemView.findViewById(R.id.dlocation);
            //contact = itemView.findViewById(R.id.partner_contact);

            AppUtils.setMontserrat(name);
            AppUtils.setMontserrat(plocation);
            AppUtils.setMontserrat(dlocation);
        }
    }
}
