package com.darewro.riderApp.view.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public abstract class BaseDialogFragment extends DialogFragment {

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {


            getDialog().setCanceledOnTouchOutside(true);
            getDialog().setCancelable(true);
            int dialogWidth = WindowManager.LayoutParams.MATCH_PARENT;
            int dialogHeight = WindowManager.LayoutParams.WRAP_CONTENT;
            Window window = getDialog().getWindow();
            window.setLayout(dialogWidth, dialogHeight);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.5f);
            window.setGravity(Gravity.CENTER);
            window.addFlags(Window.FEATURE_NO_TITLE);
            ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable background = new InsetDrawable(colorDrawable, 20);
            window.setBackgroundDrawable(background);


            /*View decorView = window.getDecorView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }*/
        }
    }

    public abstract void initializeViews(View view);

    public abstract void setListeners();

}
