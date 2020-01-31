package com.gidtech.dailingscreen.utils;

import android.telecom.Call;

import com.gidtech.dailingscreen.activity.DailingActivity;

public class CallCallBack extends Call.Callback{
    @Override
    public void onStateChanged(Call call, int state) {
        super.onStateChanged(call, state);
    }

    @Override
    public void onCallDestroyed(Call call) {
        super.onCallDestroyed(call);
    }

    @Override
    public void onDetailsChanged(Call call, Call.Details details) {
        super.onDetailsChanged(call, details);
    }
}
