package com.jackpot.follow_init;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by KWAK on 2018-06-03.
 */

public class Alarm_service extends Service {
    MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        intent.getStringExtra("13");
        return null;
    }

    public void onCreate(){
        super.onCreate();
        mediaPlayer= MediaPlayer.create(this, R.raw.alarm);
        Intent toAlarmPlaying = new Intent(this,Alarm_playing.class);
        toAlarmPlaying.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        startActivity(toAlarmPlaying);
    }

    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.stop();
        Intent toMain=new Intent(this,MainActivity.class);
        toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toMain);
    }
}