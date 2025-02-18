package com.darewro.rider.view.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.darewro.rider.R;
import com.darewro.rider.view.listeners.AlertDialogDeliverOrderResponseListener;
import com.darewro.rider.view.listeners.AlertDialogResponseListener;
import com.darewro.rider.view.listeners.NGROKAlertDialogResponseListener;
import com.franmontiel.localechanger.LocaleChanger;
import com.franmontiel.localechanger.utils.ActivityRecreationHelper;

import java.util.Locale;

public class AlertDialogUtils {

    public static final int ALERT_DIALOG_SEND_V_CODE = 0;
    public static final int ALERT_DIALOG_CHANGE_STATUS = 1;
    public static final int ALERT_DIALOG_ACCEPT_REJECT = 2;
    public static final int ALERT_DIALOG_ABOUT = 3;
    public static final int ALERT_DIALOG_WARNING = 5;
    public static final int ALERT_DIALOG_LOGOUT = 4;
    public static final int ALERT_DIALOG_GPS = 6;
    public static final int ALERT_DIALOG_DISCLAIMER = 7;
    public static final int ALERT_DIALOG_PAYMENT_RECEIVED = 8;
    public static int REQUEST_TIME_OUT = 30000;
    private static SeekBar seekBar;
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    ;
    private static boolean wasPlaying = false;

    ProgressDialog progressDialog;
    private static final AlertDialogUtils ourInstance = new AlertDialogUtils();

    public static AlertDialogUtils getInstance() {
        return ourInstance;
    }

    public static void showLanguageChangeAlertDialog(final Context context, final AlertDialogResponseListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.layout_alert_dialog, null);
        ((TextView) alertView.findViewById(R.id.title)).setText(R.string.change_language);
        ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(R.string.change_language_detail);
        ((Button) alertView.findViewById(R.id.yes)).setText(R.string.english);
        ((Button) alertView.findViewById(R.id.no)).setText(R.string.pashto);
        builder.setView(alertView);


        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
        AppUtils.setMontserrat(alertView.findViewById(R.id.confirmation_msg));
        AppUtils.setMontserrat(alertView.findViewById(R.id.yes));
        AppUtils.setMontserrat(alertView.findViewById(R.id.no));

        builder.setCancelable(false);
        final AlertDialog myDialog = builder.create();

        alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.IS_LOCALE_SELECTED, true, context);
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCALE_SELECTED, "en", context);
                Locale locale = new Locale("en", "US");
                LocaleChanger.setLocale(locale);
                ActivityRecreationHelper.recreate(((Activity) context), true);

                listener.OnSuccess();
                myDialog.dismiss();

                //switchActivity(context, OnBoardingActivity.class,null);
            }
        });

        alertView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.IS_LOCALE_SELECTED, true, context);
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCALE_SELECTED, "ps", context);
                Locale locale = new Locale("ps", "");
                LocaleChanger.setLocale(locale);
                ActivityRecreationHelper.recreate(((Activity) context), true);

                listener.OnSuccess();
                myDialog.dismiss();

//                switchActivity(context, OnBoardingActivity.class,null);
            }
        });

        myDialog.show();
    }


    public static void showNotificationDialog(final Context context, final int alertId, String title, String text, final AlertDialogResponseListener listener, String url) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.layout_notif_dialog, null);
        ((TextView) alertView.findViewById(R.id.title)).setText(title);
        ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(text);
        if (TextUtils.isEmpty(title)) {
            alertView.findViewById(R.id.title).setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(text)) {
            alertView.findViewById(R.id.confirmation_msg).setVisibility(View.GONE);
        }
        ((Button) alertView.findViewById(R.id.yes)).setText(context.getResources().getText(R.string.ok));
        ((Button) alertView.findViewById(R.id.no)).setText(context.getResources().getText(R.string.cancel));
        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
        AppUtils.setMontserrat(alertView.findViewById(R.id.confirmation_msg));
        AppUtils.setMontserrat(alertView.findViewById(R.id.yes));
        AppUtils.setMontserrat(alertView.findViewById(R.id.no));
        final ImageView imageView = ((ImageView) alertView.findViewById(R.id.image));
        Glide.with((AppCompatActivity) context)
                .load(url)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.no_image));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageView.setImageDrawable(resource);
                        return true;
                    }
                })
                .apply(new RequestOptions().timeout((int) REQUEST_TIME_OUT)/*.override(imageUtils.getWidth(),imageUtils.getHeight())*/)
                .into(imageView);
        builder.setView(alertView);

        builder.setCancelable(false);
        final android.app.AlertDialog myDialog = builder.create();
        myDialog.setCancelable(false);

        alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnSuccess(alertId);
                myDialog.dismiss();
            }
        });

        alertView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnCancel(alertId);
                myDialog.cancel();
            }
        });

        myDialog.show();
    }


    public static void showConfirmationAlertDialog(Context context, final int alertId, String title, String text, final AlertDialogResponseListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.layout_alert_dialog, null);
        ((TextView) alertView.findViewById(R.id.title)).setText(title);
        ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(text);
        builder.setView(alertView);


        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
        AppUtils.setMontserrat(alertView.findViewById(R.id.confirmation_msg));
        AppUtils.setMontserrat(alertView.findViewById(R.id.yes));
        AppUtils.setMontserrat(alertView.findViewById(R.id.no));
        ((Button) alertView.findViewById(R.id.yes)).setText(context.getResources().getString(R.string.ok));
        ((Button) alertView.findViewById(R.id.no)).setText(context.getResources().getString(R.string.cancel));
        builder.setCancelable(false);
        final AlertDialog myDialog = builder.create();

        alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnSuccess(alertId);
                myDialog.dismiss();
            }
        });

        alertView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnSuccess(alertId);
                myDialog.cancel();
            }
        });

        myDialog.show();
    }

    public static void showConfirmationAlertDialog(Context context, final int alertId, String title, String text, String yesText, String noText, final AlertDialogResponseListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.layout_alert_dialog, null);
        ((TextView) alertView.findViewById(R.id.title)).setText(title);
        ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(text);
        ((Button) alertView.findViewById(R.id.yes)).setText(yesText);
        ((Button) alertView.findViewById(R.id.no)).setText(noText);
        alertView.findViewById(R.id.no).setVisibility(View.GONE);

        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
        AppUtils.setMontserrat(alertView.findViewById(R.id.confirmation_msg));
        AppUtils.setMontserrat(alertView.findViewById(R.id.yes));
        AppUtils.setMontserrat(alertView.findViewById(R.id.no));

        builder.setView(alertView);

        builder.setCancelable(false);
        final AlertDialog myDialog = builder.create();

        alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.OnSuccess(alertId);
                if (alertId == ALERT_DIALOG_GPS)
                    SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.IS_GPS_DIALOG_VISIBLE, false, context);
                myDialog.dismiss();
            }
        });

        alertView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnCancel(alertId);
                myDialog.cancel();
            }
        });

        myDialog.show();
    }

    public static void showConfirmationAlertDialog(Context context, final int alertId, String title, String text, final Object object, final Object object2, final AlertDialogResponseListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.layout_alert_dialog, null);
        ((TextView) alertView.findViewById(R.id.title)).setText(title);
        ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(text);
        builder.setView(alertView);

        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
        AppUtils.setMontserrat(alertView.findViewById(R.id.confirmation_msg));
        AppUtils.setMontserrat(alertView.findViewById(R.id.yes));
        AppUtils.setMontserrat(alertView.findViewById(R.id.no));

        builder.setCancelable(false);
        final AlertDialog myDialog = builder.create();

        alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.OnSuccess(alertId, object, object2);
                myDialog.dismiss();
            }
        });

        alertView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.cancel();
            }
        });

        myDialog.show();
    }

    @SuppressLint("ResourceType")
    public static void showConfirmationAlertDialog(Context context, final int alertId, String title, String text, final AlertDialogResponseListener listener, final Object status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.layout_alert_dialog, null);
        ((TextView) alertView.findViewById(R.id.title)).setText(title);
        ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(text);
        ((Button) alertView.findViewById(R.id.yes)).setText(context.getResources().getString(R.string.yes));
        ((Button) alertView.findViewById(R.id.no)).setText(context.getResources().getString(R.string.no));
        builder.setView(alertView);


        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
        AppUtils.setMontserrat(alertView.findViewById(R.id.confirmation_msg));
        AppUtils.setMontserrat(alertView.findViewById(R.id.yes));
        AppUtils.setMontserrat(alertView.findViewById(R.id.no));

        builder.setCancelable(false);
        final AlertDialog myDialog = builder.create();

        alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnSuccess(alertId, status);
                myDialog.dismiss();
            }
        });

        alertView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnCancel(alertId);
                myDialog.cancel();
            }
        });

        myDialog.show();
    }

    public static void showAlertDialog(Context context, final int alertId, String title, String text, String yesText, final AlertDialogResponseListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.layout_alert_dialog, null);
        ((TextView) alertView.findViewById(R.id.title)).setText(title);
        ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(text);
        ((Button) alertView.findViewById(R.id.yes)).setText(yesText);
        alertView.findViewById(R.id.no).setVisibility(View.GONE);

        builder.setView(alertView);


        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
        AppUtils.setMontserrat(alertView.findViewById(R.id.confirmation_msg));
        AppUtils.setMontserrat(alertView.findViewById(R.id.yes));
        AppUtils.setMontserrat(alertView.findViewById(R.id.no));

        builder.setCancelable(false);
        final AlertDialog myDialog = builder.create();

        alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnSuccess(alertId);
                myDialog.dismiss();
            }
        });

        alertView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnCancel(alertId);
                myDialog.cancel();
            }
        });

        myDialog.show();
    }

//    public static void showAlertDialog(final Context context, final int alertId, String close, String yesText, final AlertDialogResponseListener listener) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        LayoutInflater inflater = LayoutInflater.from(context);
//        final View alertView = inflater.inflate(R.layout.layout_alert_dialog_voice_note, null);
//        ((Button)alertView.findViewById(R.id.button)).setText("Play");
//
//        ((Button)alertView.findViewById(R.id.close)).setText(close);
//
//        alertView.findViewById(R.id.close).setVisibility(View.VISIBLE);
//
//        final TextView seekBarHint = alertView.findViewById(R.id.textView);
//
//        seekBar = alertView.findViewById(R.id.seekbar);
//
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//                seekBarHint.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
//                seekBarHint.setVisibility(View.VISIBLE);
//                int x = (int) Math.ceil(progress / 1000f);
//
//                if (x < 10)
//                    seekBarHint.setText("0:0" + x);
//                else
//                    seekBarHint.setText("0:" + x);
//
//                double percent = progress / (double) seekBar.getMax();
//                int offset = seekBar.getThumbOffset();
//                int seekWidth = seekBar.getWidth();
//                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
//                int labelWidth = seekBarHint.getWidth();
//                seekBarHint.setX(offset + seekBar.getX() + val
//                        - Math.round(percent * offset)
//                        - Math.round(percent * labelWidth / 2));
//
//                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
//                    clearMediaPlayer();
//                    ((Button)alertView.findViewById(R.id.button)).setText("Play");
//                    seekBar.setProgress(0);
//                }
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//
//                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                    mediaPlayer.seekTo(seekBar.getProgress());
//                }
//            }
//        });
//
//        builder.setView(alertView);
//
//
//        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
//        AppUtils.setMontserrat(alertView.findViewById(R.id.confirmation_msg));
//        AppUtils.setMontserrat(alertView.findViewById(R.id.yes));
//        AppUtils.setMontserrat(alertView.findViewById(R.id.no));
//
//        builder.setCancelable(false);
//        final AlertDialog myDialog = builder.create();
//
//        final SeekBar finalSeekBar = seekBar;
//        alertView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //listener.OnSuccess(alertId);
//                try {
//
//
//                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                        clearMediaPlayer();
//                        finalSeekBar.setProgress(0);
//                        wasPlaying = true;
//                        ((Button)alertView.findViewById(R.id.button)).setText("Play");
//
//                    }
//
//
//                    if (!wasPlaying) {
//
//                        if (mediaPlayer == null) {
//                            mediaPlayer = new MediaPlayer();
//                        }
//
//                        ((Button)alertView.findViewById(R.id.button)).setText("Pause");
//
//                        AssetFileDescriptor descriptor = getAssets().openFd("suits.mp3");
//                        mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
//                        descriptor.close();
//
//                        mediaPlayer.prepare();
//                        mediaPlayer.setVolume(0.5f, 0.5f);
//                        mediaPlayer.setLooping(false);
//                        finalSeekBar.setMax(mediaPlayer.getDuration());
//
//                        mediaPlayer.start();
//                        new Thread(context).start();
//
//                    }
//
//                    wasPlaying = false;
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                }
//                //myDialog.dismiss();
//            }
//        });
//
//        alertView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.OnCancel(alertId);
//                myDialog.cancel();
//            }
//        });
//
//        myDialog.show();
//    }

    public static void showPaymentReceivedDialog(Context context, final int alertId, String title, String text, String yesText, String noText, final AlertDialogResponseListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.layout_alert_dialog, null);
        ((TextView) alertView.findViewById(R.id.title)).setText(title);
        ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(text);
        ((Button) alertView.findViewById(R.id.yes)).setText(yesText);
        ((Button) alertView.findViewById(R.id.no)).setText(noText);

        builder.setView(alertView);


        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
        AppUtils.setMontserrat(alertView.findViewById(R.id.confirmation_msg));
        AppUtils.setMontserrat(alertView.findViewById(R.id.yes));
        AppUtils.setMontserrat(alertView.findViewById(R.id.no));

        builder.setCancelable(false);
        final AlertDialog myDialog = builder.create();

        alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnSuccess(alertId);
                myDialog.dismiss();
            }
        });

        alertView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnCancel(alertId);
                myDialog.cancel();
            }
        });

        myDialog.show();
    }

    public static void showAlertDialog(final Context context, final int alertId, String title, String text, String yesText, final AlertDialogResponseListener listener, boolean openStore) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.layout_alert_dialog, null);
        ((TextView) alertView.findViewById(R.id.title)).setText(title);
        ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(text);
        ((Button) alertView.findViewById(R.id.yes)).setText(yesText);
        alertView.findViewById(R.id.no).setVisibility(View.GONE);

        builder.setView(alertView);


        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
        AppUtils.setMontserrat(alertView.findViewById(R.id.confirmation_msg));
        AppUtils.setMontserrat(alertView.findViewById(R.id.yes));
        AppUtils.setMontserrat(alertView.findViewById(R.id.no));

        builder.setCancelable(false);
        final AlertDialog myDialog = builder.create();

        alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                if (listener != null)
                    listener.OnSuccess(alertId);
                myDialog.dismiss();
            }
        });

        alertView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnCancel(alertId);
                myDialog.cancel();
            }
        });

        myDialog.show();
    }

    public static void showNotificationAlertDialog(Context context, String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.layout_alert_dialog, null);
        ((TextView) alertView.findViewById(R.id.title)).setText(title);
        ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(text);
        ((Button) alertView.findViewById(R.id.yes)).setText(context.getResources().getText(R.string.close));
        alertView.findViewById(R.id.no).setVisibility(View.GONE);

        builder.setView(alertView);


        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
        AppUtils.setMontserrat(alertView.findViewById(R.id.confirmation_msg));
        AppUtils.setMontserrat(alertView.findViewById(R.id.yes));
        AppUtils.setMontserrat(alertView.findViewById(R.id.no));

        builder.setCancelable(false);
        final AlertDialog myDialog = builder.create();

        alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        alertView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.cancel();
            }
        });

        myDialog.show();
    }

    public static void makeAlertDialog(String info, String s, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.layout_alert_dialog, null);
        ((TextView) alertView.findViewById(R.id.title)).setText(info);
        ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(s);
        ((Button) alertView.findViewById(R.id.yes)).setText(context.getResources().getString(R.string.enable));
        ((Button) alertView.findViewById(R.id.no)).setText(context.getResources().getString(R.string.no));
        alertView.findViewById(R.id.no).setVisibility(View.GONE);
        builder.setView(alertView);


        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
        AppUtils.setMontserrat(alertView.findViewById(R.id.confirmation_msg));
        AppUtils.setMontserrat(alertView.findViewById(R.id.yes));
        AppUtils.setMontserrat(alertView.findViewById(R.id.no));

        builder.setCancelable(false);
        final AlertDialog myDialog = builder.create();

        alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                context.startActivity(intent);
                myDialog.dismiss();
            }
        });

        myDialog.show();
    }

    public static void makeAlertDialogNGROK(String title, String message, final Context context, final NGROKAlertDialogResponseListener ngrokAlertDialogResponseListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View alertView = inflater.inflate(R.layout.layout_alert_dialog_edittext, null);
        ((TextView) alertView.findViewById(R.id.title)).setText(title);
        final TextView http = alertView.findViewById(R.id.http);
        final EditText url = alertView.findViewById(R.id.url);
        final TextView ngrok = alertView.findViewById(R.id.ngrok);
        url.setText(message);
        ((Button) alertView.findViewById(R.id.yes)).setText(context.getResources().getString(R.string.proceed));
        ((Button) alertView.findViewById(R.id.no)).setText(context.getResources().getString(R.string.no));
        alertView.findViewById(R.id.no).setVisibility(View.GONE);
        builder.setView(alertView);


        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
        AppUtils.setMontserrat(alertView.findViewById(R.id.url));
        AppUtils.setMontserrat(alertView.findViewById(R.id.http));
        AppUtils.setMontserrat(alertView.findViewById(R.id.ngrok));
        AppUtils.setMontserrat(alertView.findViewById(R.id.yes));
        AppUtils.setMontserrat(alertView.findViewById(R.id.no));

        builder.setCancelable(false);
        final AlertDialog myDialog = builder.create();

        alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ngrokAlertDialogResponseListener.OnNGROKSuccess(http.getText().toString() + url.getText().toString() + ngrok.getText().toString());
                myDialog.dismiss();
            }
        });

        myDialog.show();
    }

    public static void deliverOrder(final Context context, String info, String s, final AlertDialogDeliverOrderResponseListener alertDialogDeliverOrderResponseListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.layout_alert_dialog_deliver, null);
        ((TextView) alertView.findViewById(R.id.title)).setText(info);
        ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(s);
        builder.setView(alertView);


        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
        AppUtils.setMontserrat(alertView.findViewById(R.id.confirmation_msg));
        AppUtils.setMontserrat(alertView.findViewById(R.id.deliver));
        AppUtils.setMontserrat(alertView.findViewById(R.id.deliver_with_pic));

        builder.setCancelable(true);
        final AlertDialog myDialog = builder.create();

        alertView.findViewById(R.id.deliver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogDeliverOrderResponseListener.OnDeliverClick();
                myDialog.dismiss();
            }
        });
        alertView.findViewById(R.id.deliver_with_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogDeliverOrderResponseListener.OnDeliverWithPictureClick();
                myDialog.dismiss();
            }
        });

        myDialog.show();
    }


    public static void showAlert(Context context, final int alertId, String text, final AlertDialogResponseListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.layout_alert_dialog, null);
        ((TextView) alertView.findViewById(R.id.title)).setText(context.getString(R.string.disclaimer));
        ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(text);
        ((Button) alertView.findViewById(R.id.yes)).setText(context.getResources().getString(R.string.accept));
        ((Button) alertView.findViewById(R.id.no)).setText(context.getResources().getString(R.string.decline));

        builder.setView(alertView);

        AppUtils.setMontserratBold(alertView.findViewById(R.id.title));
        AppUtils.setMontserrat(alertView.findViewById(R.id.confirmation_msg));
        AppUtils.setMontserrat(alertView.findViewById(R.id.yes));
        AppUtils.setMontserrat(alertView.findViewById(R.id.no));

        builder.setCancelable(false);
        final AlertDialog myDialog = builder.create();
        alertView.findViewById(R.id.no).setVisibility(View.GONE);
        alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnSuccess(alertId);
                myDialog.dismiss();
            }
        });

        alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnCancel(alertId);
                myDialog.dismiss();
            }
        });

        myDialog.show();
    }

    public void showLoading(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.getWindow().setBackgroundDrawable
                (new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(new ProgressBar(context));
    }

    public void showLoadingWithMsg(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading files....");
        progressDialog.show();
        //progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //progressDialog.setContentView(new ProgressBar(context));
    }

    public void showLoadingWithMsgTask(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Refreshing Tasks....");
        progressDialog.show();
        //progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //progressDialog.setContentView(new ProgressBar(context));
    }

    public void showLoading(String message, Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void hideLoading() {
        if (progressDialog != null)
            progressDialog.setCancelable(true);

        try {
            if (progressDialog != null && progressDialog.isShowing()) {

                progressDialog.dismiss();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

}
