package com.darewro.rider.view.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.darewro.rider.R;
import com.darewro.rider.view.utils.AppUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Hassan on 3/26/2016.
 */
public class ToastFragment extends android.app.DialogFragment implements DialogInterface.OnShowListener {
    public static final long TOAST_DEFAULT_LENGTH = 5000;
    public static long TOAST_LENGTH = 5000;
    View inflatedView;
    FrameLayout closeButton;
    ImageView toastIcon;
    String toastMessage;
    TextView toastText;
    Timer timer;
    private boolean shouldShowIcon = true;
    private int iconDrawable = 0;
    private LinearLayout layout;

    public void setShouldShowIcon(boolean shouldShowIcon) {
        this.shouldShowIcon = shouldShowIcon;
    }

    public void setIconDrawable(int iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public void setTOAST_LENGTH(long TOAST_LENGTH) {
        this.TOAST_LENGTH = TOAST_LENGTH;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.toast_layout, container, false);
        return inflatedView;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeAll(view);

    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null) {
            return;
        }

        getDialog().setCanceledOnTouchOutside(false);
        int dialogWidth = WindowManager.LayoutParams.MATCH_PARENT;
        int dialogHeight = WindowManager.LayoutParams.WRAP_CONTENT;

        Window window = getDialog().getWindow();
        window.setLayout(dialogWidth, dialogHeight);
        window.setGravity(Gravity.TOP);
        window.addFlags(Window.FEATURE_NO_TITLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.addFlags(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable background = new ColorDrawable(getResources().getColor(R.color.dark_grey));
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.dark_grey));
            window.setNavigationBarColor(getResources().getColor(R.color.darker_grey));
            window.setBackgroundDrawable(background);
        } else {
            Drawable background = new ColorDrawable(getResources().getColor(R.color.dark_grey));
            window.setBackgroundDrawable(background);
        }

    }

    private void initializeAll(View v) {

        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }



        layout = (LinearLayout) v.findViewById(R.id.layout);
        FrameLayout toastTextLayout = (FrameLayout) v.findViewById(R.id.toast_text_layout);
        closeButton = (FrameLayout) v.findViewById(R.id.toast_closeButton);
        toastIcon = (ImageView) v.findViewById(R.id.toast_icon);
        toastText = (TextView) v.findViewById(R.id.toast_text);

        if (statusBarHeight > AppUtils.dp2px(getResources(),24f)) {
           /* LinearLayout.LayoutParams topbarLp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            topbarLp.setMargins(0, statusBarHeight, 0, 0);
            layout.setLayoutParams(topbarLp);*/

//            FrameLayout.LayoutParams topbarLp = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            LinearLayout.LayoutParams topbarLp = (LinearLayout.LayoutParams) toastTextLayout.getLayoutParams();
//            topbarLp.setMargins(0, statusBarHeight, 0, 0);
//            toastTextLayout.setLayoutParams(topbarLp);

            toastText.setMinimumHeight(toastText.getMinHeight()+statusBarHeight);
//            layout.setMinimumHeight(layout.getMinimumHeight()+statusBarHeight);
        }


        toastText.setText(toastMessage);


//        closeButton.setVisibility(View.VISIBLE);
        /*closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                }

                dismissAllowingStateLoss();
            }
        });*/
        if (!shouldShowIcon) {
            toastIcon.setVisibility(View.INVISIBLE);
        }
        if (iconDrawable != 0) {
            toastIcon.setImageResource(iconDrawable);
        }

    }

    public void setToastMessage(String toastMessage) {
        this.toastMessage = toastMessage;
//        this.toastMessage = App.getInstance().getResources().getString(R.string.large_text);
    }


    private void startTimer() {
        class CloseTimer extends TimerTask {

            @Override
            public void run() {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getDialog() != null) {
                                dismissAllowingStateLoss();
                            }
                        }
                    });
                }
            }
        }

        timer = new Timer();
        timer.schedule(new CloseTimer(), TOAST_LENGTH);
    }


    @Override
    public void onShow(DialogInterface dialog) {
        startTimer();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(this);
        return dialog;
    }


    @Override
    public void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
    }
}

