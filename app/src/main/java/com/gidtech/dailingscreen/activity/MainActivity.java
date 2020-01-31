package com.gidtech.dailingscreen.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.role.RoleManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.widget.Toast;

import com.gidtech.dailingscreen.R;
import com.gidtech.dailingscreen.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ID = 1;

    public static final int NOTIFICATION_ID = 888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        // Check wither this app was set as the default dialer
        boolean isDefaultDialer = Utils.checkDefaultDialer(this);
        if (isDefaultDialer) checkPermissions(null);



//        finish();
//        moveTaskToBack(true);

//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(1);
    }

    private void checkPermissions(int[] grantResults) {
        if (
                (grantResults != null && Utils.checkPermissionsGranted(grantResults)) ||
                        Utils.checkPermissionsGranted(this, Utils.MUST_HAVE_PERMISSIONS)) {
            //If granted

        } else {
            Utils.askForPermissions(this, Utils.MUST_HAVE_PERMISSIONS);
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.DEFAULT_DIALER_RC) {
            checkPermissions(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermissions(grantResults);
    }


}
