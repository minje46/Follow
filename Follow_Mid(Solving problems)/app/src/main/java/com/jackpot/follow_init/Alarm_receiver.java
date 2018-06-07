package com.jackpot.follow_init;

/**
 * Created by KWAK on 2018-06-03.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Alarm_receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = intent.getStringExtra("13");
        int id = intent.getIntExtra("id", -1);
        Database_overall db = Database_overall.getInstance(context);
        db.delete(String.valueOf(id));
        Intent toService = new Intent(context, Alarm_service.class);
        toService.putExtra("13",key);
        context.startService(toService);
    }
}
