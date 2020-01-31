package com.gidtech.dailingscreen.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.gidtech.dailingscreen.R;


/**
 * Template class meant to include functionality for your Messaging App. (This project's main focus
 * is on Notification Styles.)
 */
public class NotificationMessageActivity extends Activity {

    private static final String TAG = "MessagingMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_message);

        // Cancel Notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(MainActivity.NOTIFICATION_ID);

        // TODO: Handle and display message/conversation from your database
        // NOTE: You can retrieve the EXTRA_REMOTE_INPUT_DRAFT sent by the system when a user
        // inadvertently closes a messaging notification to pre-populate the reply text field so
        // the user can finish their reply.
    }
}
