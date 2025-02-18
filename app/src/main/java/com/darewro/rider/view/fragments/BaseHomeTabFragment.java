package com.darewro.rider.view.fragments;

import android.os.Bundle;
import android.util.Log;

import com.darewro.rider.view.activities.MainActivity;

public abstract class BaseHomeTabFragment extends BaseFragment {
    public static MainActivity activity;
    private static String SEARCHED_TEXT = "";
    protected String type;
    protected int tab;

    public static String getSearchedText() {
        return SEARCHED_TEXT;
    }

    public static void setSearchedText(String searchedText) {
        SEARCHED_TEXT = searchedText;
//        TODO  refresh fragment with search filter
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    public abstract void filter(String query);

    public abstract void filter();

    public boolean isUserVisible() {
        if (!getUserVisibleHint()) {
            Log.i(this.getClass().getName(), "I am not visible");
        } else {
            Log.i(this.getClass().getName(), "I am visible");
        }
        return getUserVisibleHint();
    }

    public abstract void LoadTabFromServer();

    public abstract void ShowHideNoLayout(boolean show);
}
