package com.darewro.rider.view.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darewro.rider.R;
import com.darewro.rider.data.api.models.allOrders.AllOrder;
import com.darewro.rider.view.activities.MainActivity;
import com.darewro.rider.view.adapters.AllOrdersAdapter;
import com.darewro.rider.view.adapters.RecyclerAdapterOrders;
import com.darewro.rider.view.customViews.ItemOffsetDecorationLTBR;
import com.darewro.rider.view.models.NearestOrder;
import com.darewro.rider.view.models.Order;
import com.darewro.rider.view.utils.AlertDialogUtils;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.SharedPreferenceHelper;

import java.util.List;

public class AllOrdersFragment extends BaseFragment {


    SwipeRefreshLayout swipeRefreshLayout = null;
    TextView noLayout;
    private RecyclerView recyclerView;
    private Parcelable recyclerViewState;

    private AllOrdersAdapter adapterOrders;

    private LinearLayoutCompat layoutRunningOrder,layoutRefresh;
    private AppCompatTextView tvRiderMessage;


    public AllOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_orders, container, false);
        initializeViews(view);
        return view;
    }

    @Override
    public void initializeViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_container);
        noLayout = view.findViewById(R.id.noLayoutTextView);
        layoutRunningOrder = view.findViewById(R.id.layoutRunningOrder);
        layoutRefresh = view.findViewById(R.id.layoutRefresh);
        tvRiderMessage = view.findViewById(R.id.tvRiderMessage);


        layoutRefresh.setOnClickListener(view1 -> ((MainActivity) getActivity()).getAllOrders());

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
                    ((MainActivity) getActivity()).getAllOrders();
                }
        );
    }
    @Override
    public void onResume() {
        super.onResume();
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
    }
    @Override
    public void setData(List<Order> orders) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setData(boolean isAllOrder, List<AllOrder> orders) {

        String orderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, getContext());

        if (orderId.equals("")) {
            layoutRunningOrder.setVisibility(View.GONE);
            if (recyclerView != null) {
                recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                adapterOrders = new AllOrdersAdapter(getActivity(), orders);
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
        } else {
            String userName = SharedPreferenceHelper.getString(SharedPreferenceHelper.USERNAME, getContext());

            layoutRunningOrder.setVisibility(View.VISIBLE);
            tvRiderMessage.setText("Hi "+userName +", "+getString(R.string.str_running_order_msg));
        }


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