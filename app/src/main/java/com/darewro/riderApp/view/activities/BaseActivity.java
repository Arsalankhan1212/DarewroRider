package com.darewro.riderApp.view.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.darewro.riderApp.view.utils.SharedPreferenceHelper;
import com.franmontiel.localechanger.LocaleChanger;
import com.darewro.riderApp.App;
import com.darewro.riderApp.R;
import com.google.firebase.messaging.FirebaseMessaging;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Window window = getWindow();

        Drawable background = ContextCompat.getDrawable(this,R.drawable.gradient_drawable);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.transparent));
        window.setNavigationBarColor(ContextCompat.getColor(this,R.color.darker_grey));
        window.setBackgroundDrawable(background);


    }




    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleChanger.configureBaseContext(newBase);
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public abstract void initializeViews();

    public abstract void setListeners();

    public abstract void handleIntent();


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}
