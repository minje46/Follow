package com.jackpot.follow_init;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("MyReceiver", "mrmrmr");
        String k=intent.getStringExtra("13");
        Intent i = new Intent(context,MyService.class);
        i.putExtra("13",k);
        context.startService(i);
    }


}
