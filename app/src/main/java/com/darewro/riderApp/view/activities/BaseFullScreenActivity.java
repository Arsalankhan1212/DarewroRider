package com.darewro.riderApp.view.activities;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.view.Window;
import android.view.WindowManager;

import com.darewro.riderApp.App;
import com.darewro.riderApp.R;

public abstract class BaseFullScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Window window = getWindow();
        Drawable background = ContextCompat.getDrawable(this, R.drawable.gradient_drawable);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,android.R.color.transparent));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.darker_grey));

        window.setBackgroundDrawable(background);

    }


}
