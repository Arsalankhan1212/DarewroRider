package com.darewro.riderApp.view.fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.darewro.riderApp.R;
import com.darewro.riderApp.data.api.models.allOrders.AllOrder;
import com.darewro.riderApp.view.activities.MainActivity;
import com.darewro.riderApp.view.adapters.RecyclerAdapterOrders;
import com.darewro.riderApp.view.customViews.ItemOffsetDecorationLTBR;
import com.darewro.riderApp.view.models.NearestOrder;
import com.darewro.riderApp.view.models.Order;
import com.darewro.riderApp.view.utils.AppUtils;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderListingFragment extends BaseFragment {


    SwipeRefreshLayout swipeRefreshLayout = null;
    TextView noLayout;
    private RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    private RecyclerAdapterOrders adapterOrders;

    public OrderListingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_listing, container, false);
        initializeViews(view);
        return view;
    }

    @Override
    public void initializeViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_container);
        noLayout = view.findViewById(R.id.noLayoutTextView);

        AppUtils.setMontserrat(noLayout);

        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new ItemOffsetDecorationLTBR());
        setListeners();
    }

    @Override
    public void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(
                () -> {
                    ((MainActivity) getActivity()).getOrders(0, 0, "", false);
                    ((MainActivity) getActivity()).getNearestOrders();
                }
        );

    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    public void setData(List<Order> orders) {

//        AppUtils.makeNotification(""+orders.toString(), getActivity());

        if (recyclerView != null) {
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
            adapterOrders = new RecyclerAdapterOrders(getActivity(), orders);
            recyclerView.setAdapter(adapterOrders);
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            adapterOrders.notifyDataSetChanged();
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (noLayout != null) {
            if (orders != null && orders.size() > 0) {
                noLayout.setVisibility(View.GONE);
            } else {
                noLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setData(boolean isAllOrder, List<AllOrder> orders) {

    }

    public int getCount() {
        if (recyclerView != null && recyclerView.getAdapter()!=null)
            return recyclerView.getAdapter().getItemCount();
        else
            return 0;
    }

    @Override
    public void setData(List<NearestOrder> nearestOrders, boolean nearest) {

    }

    public void setError() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (noLayout != null) {
            if (adapterOrders != null && adapterOrders.getItemCount() > 0) {
                noLayout.setVisibility(View.GONE);
            } else {
                noLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
