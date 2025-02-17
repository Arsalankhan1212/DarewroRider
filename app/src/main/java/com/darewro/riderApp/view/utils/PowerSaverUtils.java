package com.darewro.riderApp.view.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.darewro.riderApp.App;
import com.darewro.riderApp.BuildConfig;
import com.darewro.riderApp.R;

import java.util.Arrays;
import java.util.List;

public class PowerSaverUtils {

    private static final List<Intent> POWERMANAGER_INTENTS = Arrays.asList(
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),

            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),

            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
//            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),

            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")).setData(Uri.fromParts("package", App.getInstance().getPackageName(), null)),
            new Intent().setClassName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"),
            new Intent().setClassName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity"),

            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setClassName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity"),

            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"),

            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),

            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity")),

            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")).setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart")),

            new Intent().setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.SHOW_APPSEC")).addCategory(Intent.CATEGORY_DEFAULT).putExtra("packageName", BuildConfig.APPLICATION_ID));


    public static void startPowerSaverIntent2(final Context context) {
        SharedPreferences settings = context.getSharedPreferences(SharedPreferenceHelper.PREFRENCESNAME, Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            boolean foundCorrectIntent = false;
            for (final Intent intent : POWERMANAGER_INTENTS) {
                if (isCallable(context, intent)) {
                    foundCorrectIntent = true;
                    /*final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
                    dontShowAgain.setText("Do not show again");
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dontShowAgain.getLayoutParams();
                    params.setMargins(10,10,10,10);
                    dontShowAgain.setLayoutParams(params);
                    dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            editor.putBoolean("skipProtectedAppCheck", isChecked);
                            editor.apply();
                        }
                    });

                    new AlertDialog.Builder(context)
                            .setTitle(Build.MANUFACTURER + " Protected Apps")
                            .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", context.getString(R.string.app_name)))
                            .setView(dontShowAgain)
                            .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    context.startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();


*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    final View alertView = inflater.inflate(R.layout.layout_alert_dialog_location_confirmation, null);
                    ((TextView) alertView.findViewById(R.id.title)).setText(Build.MANUFACTURER + " Protected Apps");
                    ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", context.getString(R.string.app_name)));
                    ((Button) alertView.findViewById(R.id.yes)).setText(R.string.go_to_settings);
                    ((Button) alertView.findViewById(R.id.no)).setText(R.string.cancel);
                    ((CheckBox) alertView.findViewById(R.id.selection)).setText(R.string.do_not_show_again);
                    ((CheckBox) alertView.findViewById(R.id.selection)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            editor.putBoolean("skipProtectedAppCheck", isChecked);
                            editor.apply();
                        }
                    });


                    AppUtils.setMontserratBold(((TextView) alertView.findViewById(R.id.title)));
                    AppUtils.setMontserrat(((TextView) alertView.findViewById(R.id.confirmation_msg)));
                    AppUtils.setMontserrat(((TextView) alertView.findViewById(R.id.yes)));
                    AppUtils.setMontserrat(((TextView) alertView.findViewById(R.id.no)));
                    AppUtils.setMontserrat(((CheckBox) alertView.findViewById(R.id.selection)));


                    builder.setView(alertView);

                    builder.setCancelable(false);
                    final AlertDialog myDialog = builder.create();

                    alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                context.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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
                    break;

                }
            }
            if (!foundCorrectIntent) {
                editor.putBoolean("skipProtectedAppCheck", true);
                editor.apply();
            }
        }
    }

    public static void startPowerSaverIntent(final Context context) {
        SharedPreferences settings = context.getSharedPreferences(SharedPreferenceHelper.PREFRENCESNAME, Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            boolean foundCorrectIntent = false;
            for (final Intent intent : POWERMANAGER_INTENTS) {
                if (isCallable(context, intent)) {
                    foundCorrectIntent = true;
                    /*final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
                    dontShowAgain.setText("Do not show again");
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dontShowAgain.getLayoutParams();
                    params.setMargins(10,10,10,10);
                    dontShowAgain.setLayoutParams(params);
                    dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            editor.putBoolean("skipProtectedAppCheck", isChecked);
                            editor.apply();
                        }
                    });

                    new AlertDialog.Builder(context)
                            .setTitle(Build.MANUFACTURER + " Protected Apps")
                            .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", context.getString(R.string.app_name)))
                            .setView(dontShowAgain)
                            .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    context.startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();


*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    final View alertView = inflater.inflate(R.layout.layout_alert_dialog_location_confirmation, null);
                    ((TextView) alertView.findViewById(R.id.title)).setText(Build.MANUFACTURER + " Protected Apps");
                    ((TextView) alertView.findViewById(R.id.confirmation_msg)).setText(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", context.getString(R.string.app_name)));
                    ((Button) alertView.findViewById(R.id.yes)).setText(R.string.go_to_settings);
                    ((Button) alertView.findViewById(R.id.no)).setText(R.string.cancel);
                    ((CheckBox) alertView.findViewById(R.id.selection)).setText(R.string.do_not_show_again);
                    ((CheckBox) alertView.findViewById(R.id.selection)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            editor.putBoolean("skipProtectedAppCheck", isChecked);
                            editor.apply();
                        }
                    });
                    builder.setView(alertView);

                    builder.setCancelable(false);
                    final AlertDialog myDialog = builder.create();

                    alertView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if (Build.MANUFACTURER.equals("OPPO")) {
                                try {
                                    context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")));
                                } catch (Exception e) {
                                    try {
                                        context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerSaverModeActivity")));
                                    } catch (Exception e1) {
                                        try {
                                            context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity")));
                                        } catch (Exception e2) {

                                        }
                                    }
                                }

                            }else
                            context.startActivity(intent);
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
                    break;
                }
            }
            if (!foundCorrectIntent) {
                editor.putBoolean("skipProtectedAppCheck", true);
                editor.apply();
            }

        }
    }

    private static boolean isCallable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
