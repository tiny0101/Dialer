package com.gidtech.dailingscreen.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.renderscript.RenderScript;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.gidtech.dailingscreen.R;
import com.gidtech.dailingscreen.activity.DailingActivity;
import com.gidtech.dailingscreen.activity.MainActivity;
import com.gidtech.dailingscreen.activity.NotificationMessageActivity;
import com.gidtech.dailingscreen.model.Contact;
import com.gidtech.dailingscreen.utils.CallCallBack;
import com.gidtech.dailingscreen.utils.CallManager;
import com.google.android.material.snackbar.Snackbar;

public class CallInServices extends InCallService {
    String YOUR_CHANNEL_ID = "gidcaller";
    String YOUR_TAG = "GidDialer";
    int YOUR_ID = 23;
    CallCallBack mCallback;

    final String channelId = "channel1";
    final String NOTIFICATION_TITLE = "Call Notification";
    final String NOTIFICATION_DESCRIPTION = "Notification description";

    public CallInServices() {

    }

    /**
     * Add Call
     *
     * @param call instance of a call
     */
    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);


        showNotification();

        Intent intent = new Intent(this, DailingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
//        call.registerCallback(mCallback);
        CallManager.call = call;
//        call.registerCallback();
//        Call.Callback callback = DailingActivity.Callback;
//        registerCallback();


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_call_black_24dp)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_DESCRIPTION)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        notificationManager.notify(MainActivity.NOTIFICATION_ID, builder.build());
    }


    /**
     * Remove call
     *
     * @param call instance of a call
     */
    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
//        call.unregisterCallback(mCallback);
    }
}
