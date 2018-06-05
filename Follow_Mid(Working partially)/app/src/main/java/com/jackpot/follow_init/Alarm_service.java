package com.jackpot.follow_init;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

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
        toAlarmPlaying.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        startActivity(toAlarmPlaying);
    }

    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.stop();
        Intent toMain=new Intent(this,MainActivity.class);
        toMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toMain);
    }
}