package com.destro.pfood.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, ExampleService.class);
        context.startService(myIntent);
    }
}
