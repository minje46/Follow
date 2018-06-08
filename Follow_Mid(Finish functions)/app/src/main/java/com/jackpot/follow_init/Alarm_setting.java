package com.jackpot.follow_init;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class Alarm_setting extends AppCompatActivity { // Main3Activity
    EditText inEvent_name;
    Button btnSet;
    DatePicker datePicker;
    TimePicker timePicker;

    int alarmId = (int)System.currentTimeMillis();

    public Alarm_setting() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_setting);

        inEvent_name = (EditText)findViewById(R.id.inEvent_name);
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        timePicker = (TimePicker)findViewById(R.id.timePicker);

        btnSet = (Button)findViewById(R.id.btnSet);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar current = Calendar.getInstance();
                Calendar cal = Calendar.getInstance();
                cal.set( // cal에 유저가 선택한 date와 time 저장, 초단위는 00으로 초기화
                        datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute(), 00);
                if (cal.compareTo(current) <= 0)
                    Toast.makeText(getApplicationContext(), "Invalid Entry", Toast.LENGTH_LONG).show();
                else {
                    String eventName = inEvent_name.getText().toString(); // 라벨이름

                    // Tab_alarm 에서 alarm set 하기 위해 Intent 에 alarm data 저장.
                    Intent toTab_alarm = new Intent();
                    Bundle bundle = new Bundle();

                    bundle.putSerializable("Calendar", cal);
                    toTab_alarm.putExtras(bundle);
                    toTab_alarm.putExtra("eventName", eventName);
                    toTab_alarm.putExtra("alarmId",alarmId);

                    setResult(RESULT_OK, toTab_alarm);
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}