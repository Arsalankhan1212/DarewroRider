package com.darewro.riderApp.view.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.darewro.riderApp.App;
import com.darewro.riderApp.R;
import com.darewro.riderApp.view.activities.MainActivity;
import com.darewro.riderApp.view.fragments.AllOrdersFragment;
import com.darewro.riderApp.view.fragments.BaseFragment;
import com.darewro.riderApp.view.fragments.MapFragment;
import com.darewro.riderApp.view.fragments.OrderListingFragment;
import com.darewro.riderApp.view.models.Order;
import com.darewro.riderApp.view.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderFragmentViewPagerAdapter extends FragmentStatePagerAdapter {
    private OrderListingFragment newTab;
    private OrderListingFragment completedTab;
    private MapFragment mapFragment;
    private AllOrdersFragment allOrdersFragment;

    private int count;

    private static List<String> titles = new ArrayList<String>();

    public OrderFragmentViewPagerAdapter(MainActivity mainActivity, FragmentManager supportFragmentManager, int tabCount) {
        super(supportFragmentManager);
        this.count = tabCount;
        newTab = new OrderListingFragment();
        completedTab = new OrderListingFragment();
        allOrdersFragment = new AllOrdersFragment();
     //   mapFragment = MapFragment.getInstance();

        titles.add(Order.NEW_TEXT);
        titles.add(Order.COMPLETED_TEXT);
        titles.add(Order.All_ORDERS);
        //titles.add(Order.NEAREST_ORDERS);

    }

    @Override
    public BaseFragment getItem(int i) {
        switch (i) {
            case 0:
                return newTab;
            case 1:
                return completedTab;
            case 2:
                return allOrdersFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(App.getInstance()).inflate(R.layout.tab_layout, null,false);
        TextView tabTextView = view.findViewById(R.id.tvtab);
        if(position == 0)
            tabTextView.setText(titles.get(position)+"\n"+newTab.getCount());
        else if(position == 1)
            tabTextView.setText(titles.get(position)+"\n"+completedTab.getCount());
        else
            tabTextView.setText(titles.get(position) +"\n"+allOrdersFragment.getCount());
        tabTextView.setTextColor(Color.WHITE);
        AppUtils.setMontserrat(tabTextView);
        return view;
    }

    public View getSelectedTabView(int position) {
        View view = LayoutInflater.from(App.getInstance()).inflate(R.layout.tab_layout, null,false);
        TextView tabTextView = view.findViewById(R.id.tvtab);
        if(position == 0)
        tabTextView.setText(titles.get(position)+"\n"+newTab.getCount());
        else if(position == 1)
        tabTextView.setText(titles.get(position)+"\n"+completedTab.getCount());
        else
            tabTextView.setText(titles.get(position) +"\n"+allOrdersFragment.getCount());

        tabTextView.setTextColor(Color.WHITE);
        Typeface typeface = ResourcesCompat.getFont(App.getInstance(), R.font.montserrat_medium);
        tabTextView.setTypeface(typeface);
      //  AppUtils.setMontserratMedium(tabTextView);
        return view;
    }

//    public View setOrderCount(int position, String title) {
//        View view = LayoutInflater.from(App.getInstance()).inflate(R.layout.tab_layout, null,false);
//        TextView tabTextView = view.findViewById(R.id.tvtab);
//        tabTextView.setText(title);
//        tabTextView.setTextColor(Color.WHITE);
//        AppUtils.setMontserratBold(tabTextView);
//        return view;
//    }
}
