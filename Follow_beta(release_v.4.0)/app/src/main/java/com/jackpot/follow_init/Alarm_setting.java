package com.jackpot.follow_init;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

public class Alarm_setting extends AppCompatActivity { // Main3Activity
    EditText inEvent_name;
    Button btnSet;

    int alarmId = (int)System.currentTimeMillis();
    int id = 0;
    int alarm_hour, alarm_minute;
    int alarm_year, alarm_month, alarm_day;
    DatePickerDialog.OnDateSetListener mDateListener;

    public Alarm_setting() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_setting);

        inEvent_name = (EditText)findViewById(R.id.inEvent_name);

        Button setTime = findViewById(R.id.btnAlarmTime);
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }

            private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                }
            };
        });

        TextView alarmAP = findViewById(R.id.alarmTimeTextAP);
        TextView alarmHour = findViewById(R.id.alarmTimeTextHour);
        TextView alarmMinute = findViewById(R.id.alarmTimeTextMinute);

        // 디폴트
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat currentHour = new SimpleDateFormat("HH");
        SimpleDateFormat currentMin = new SimpleDateFormat("mm");

        if(Integer.parseInt(currentHour.format(date)) > 12) alarmAP.setText("오후");
        else alarmAP.setText("오전");

        alarmHour.setText(currentHour.format(date));
        alarmMinute.setText(currentMin.format(date));

        Button btnAlarm = findViewById(R.id.btnAlarmDate);
        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(Alarm_setting.this, android.R.style.Theme_Holo_Dialog_MinWidth, mDateListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                alarm_year = year;
                alarm_month = month;
                alarm_day = dayOfMonth;

                TextView alarmYear = findViewById(R.id.alarmDateTextYear);
                TextView alarmMonth = findViewById(R.id.alarmDateTextMonth);
                TextView alarmDay = findViewById(R.id.alarmDateTextDay);
                alarmYear.setText(String.valueOf(year));
                alarmMonth.setText(String.valueOf(month + 1));
                alarmDay.setText(String.valueOf(dayOfMonth));
             }
        };

        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        btnSet = (Button)findViewById(R.id.btnSet);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar current = Calendar.getInstance();
                Calendar cal = Calendar.getInstance();
                cal.set( // cal에 유저가 선택한 date와 time 저장, 초단위는 00으로 초기화
                        alarm_year,
                        alarm_month,
                        alarm_day,
                        alarm_hour,
                        alarm_minute, 00);
                if (cal.compareTo(current) <= 0)
                    Toast.makeText(getApplicationContext(), "Invalid Entry", Toast.LENGTH_LONG).show();
                else {
                    String eventName = inEvent_name.getText().toString(); // 라벨이름

                    // Tab_alarm 에서 alarm set 하기 위해 Intent 에 alarm data 저장.
                    Intent toTab_alarm = new Intent();
                    Bundle bundle = new Bundle();

                    Log.d("dateLL", "2Year : " +String.valueOf(alarm_year) + "Month : " +String.valueOf(alarm_month) +"Day : " +String.valueOf(alarm_day));

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
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
            TextView alarmAP = findViewById(R.id.alarmTimeTextAP);
            TextView alarmHour = findViewById(R.id.alarmTimeTextHour);
            TextView alarmMinute = findViewById(R.id.alarmTimeTextMinute);


            alarm_hour = hourOfDay;
            alarm_minute = min;

            int temp = alarm_hour;
            if (alarm_hour >= 12) {
                alarmAP.setText("오후");
                temp = alarm_hour - 12;
            } else alarmAP.setText("오전");

            alarmHour.setText(String.valueOf(temp));

            if (alarm_minute < 10) alarmMinute.setText("0" + String.valueOf(alarm_minute));
            else alarmMinute.setText(String.valueOf(alarm_minute));
        }
    };

    @Override
    protected Dialog onCreateDialog(int temp_id) {
        return new TimePickerDialog(this, mTimeSetListener, alarm_hour, alarm_minute, true);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}