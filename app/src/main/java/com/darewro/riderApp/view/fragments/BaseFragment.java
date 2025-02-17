package com.darewro.riderApp.view.fragments;

import androidx.fragment.app.Fragment;
import android.view.View;

import com.darewro.riderApp.data.api.models.allOrders.AllOrder;
import com.darewro.riderApp.view.models.NearestOrder;
import com.darewro.riderApp.view.models.Order;

import java.util.List;

public abstract class BaseFragment extends Fragment {

    public abstract void initializeViews(View view);

    public abstract void setListeners();

    public abstract void setData(List<Order> orders);
    public abstract void setData(boolean isAllOrder,List<AllOrder> orders);

    public abstract void setData(List<NearestOrder> nearestOrders, boolean nearest);

    public abstract void setError();
    }
