package com.darewro.riderApp.view.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.darewro.riderApp.R;
import com.darewro.riderApp.view.activities.OrderDetailActivity;
import com.darewro.riderApp.view.models.Order;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.LocationUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class RecyclerAdapterNotifications extends RecyclerView.Adapter<RecyclerAdapterNotifications.ViewHolder> {
    Activity appCompatActivity;
    JSONArray arr;
    int layoutResourceId = R.layout.recycler_item_notification;

    public RecyclerAdapterNotifications(Activity appCompatActivity, JSONArray arr) {
        this.appCompatActivity = appCompatActivity;
        this.arr = arr;
    }

    @NonNull
    @Override
    public RecyclerAdapterNotifications.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(appCompatActivity).inflate(layoutResourceId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterNotifications.ViewHolder viewHolder, int position) {
        String title="",message="";

        try {
            title = this.arr.getJSONObject(position).getString("title");
            message = this.arr.getJSONObject(position).getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewHolder.title.setText(title);
        viewHolder.message.setText(message);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return arr.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title,message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            message = itemView.findViewById(R.id.message);

        }
    }
}
