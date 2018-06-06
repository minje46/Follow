package com.jackpot.follow_init;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Calendar;

/**
 * Created by KWAK on 2018-06-03.
 */

public class Alarm_playing extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_playing);

        TextView tv=(TextView)findViewById(R.id.textView2);
        Calendar now= Calendar.getInstance();
        int hour=now.get(Calendar.HOUR_OF_DAY);
        if(hour>12) {
            hour = hour - 12;
            int minute = now.get(Calendar.MINUTE);
            assert tv != null;
            tv.setText(hour + ":" + minute+" PM");
        }
        else if (hour == 12){
            int minute = now.get(Calendar.MINUTE);
            assert tv != null;
            tv.setText(hour+ ":" + minute + " PM");
        }
        else{
            int minute = now.get(Calendar.MINUTE);
            assert tv != null;
            tv.setText(hour + ":" + minute+" AM");
        }

        ImageView image = (ImageView)findViewById(R.id.imageView);
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        assert image != null;
        image.startAnimation(animation1);
    }

    // stop버튼 클릭
    public void fun(View v) {
        Intent i=new Intent(this, Alarm_service.class);
        stopService(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(this, Alarm_service.class);
        stopService(i);
        finish();
    }
}
