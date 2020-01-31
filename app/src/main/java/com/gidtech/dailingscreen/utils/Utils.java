package com.gidtech.dailingscreen.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telecom.TelecomManager;
import android.view.View;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.MODIFY_PHONE_STATE;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.RECORD_AUDIO;

public class Utils {
    public static final int DEFAULT_DIALER_RC = 11;
    public static final int PERMISSION_RC = 10;

    public static Locale sLocale;

    public static final String[] MUST_HAVE_PERMISSIONS = {CALL_PHONE, READ_CONTACTS, READ_CALL_LOG, MODIFY_AUDIO_SETTINGS,RECORD_AUDIO, MODIFY_PHONE_STATE };


    public static void setUpLocale(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Utils.sLocale = context.getResources().getSystem().getConfiguration().getLocales().get(0);
        } else {
            Utils.sLocale = context.getResources().getSystem().getConfiguration().locale;
        }
    }

    public static boolean checkDefaultDialer(AppCompatActivity activity) {
        // Prompt the user with a dialog to select this app to be the default phone app
        String packageName = activity.getApplication().getPackageName();
        if (!activity.getSystemService(TelecomManager.class).getDefaultDialerPackage().equals(packageName)) {
            Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                    .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName);
            activity.startActivityForResult(intent, DEFAULT_DIALER_RC);
            return false;
        }
        return true;
    }

    /**
     * Checks for granted permission but by a single string (single permission)
     *
     * @param permission
     * @return boolean
     */
    public static boolean checkPermissionsGranted(Context context, String permission) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(
                context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check for permissions by a given list
     *
     * @param permissions
     * @return boolean
     */
    public static boolean checkPermissionsGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (!checkPermissionsGranted(context, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check for permissions by a given list
     *
     * @param grantResults
     * @return boolean
     */
    public static boolean checkPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED)
                return false;
        }
        return true;
    }

    public static void askForPermission(FragmentActivity activity, String permission) {
        askForPermissions(activity, new String[]{permission});
    }

    /**
     * Asks for permissions by a given list
     *
     * @param activity
     * @param permissions
     */
    public static void askForPermissions(FragmentActivity activity, String[] permissions) {
        activity.requestPermissions(permissions, PERMISSION_RC);
    }

    /**
     * Toggle the active state of a view
     *
     * @param view the view to toggle
     */
    public static void toggleViewActivation(View view) {
        view.setActivated(!view.isActivated());
    }

}
