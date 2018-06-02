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

public class Alarm_setting extends Fragment { // Main3Activity
    DatePicker datePicker;
    TimePicker timePicker;
    Button b1;
    EditText editText;
    Obj_alarm ad;
    SQLiteHelper obj;
    ArrayList<String> items;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    final static int RQS_1=1;
    int num = 1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView =(ViewGroup)inflater.inflate(R.layout.activity_main3, container, false);

        datePicker=(DatePicker)rootView.findViewById(R.id.datePicker);
        timePicker=(TimePicker)rootView.findViewById(R.id.timePicker);
        b1=(Button)rootView.findViewById(R.id.button2);
        editText = (EditText)rootView.findViewById(R.id.editText);
        items = new ArrayList<String>();
        obj = new SQLiteHelper(rootView.getContext());

        Calendar now=Calendar.getInstance();
        datePicker.init(
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH),null
        );
        timePicker.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(now.get(Calendar.MINUTE));

        Log.e("캘린더", "Hour of day: " + Calendar.YEAR);
        Log.e("캘린더", "Hour of day: " + Calendar.MONTH);
        Log.e("캘린더", "Hour of day: " + Calendar.DAY_OF_MONTH);
        Log.e("캘린더", "Hour of day: " + Calendar.HOUR_OF_DAY);
        Log.e("캘린더", "Hour of day: " + Calendar.MINUTE);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar current=Calendar.getInstance();
                Calendar cal=Calendar.getInstance();
                cal.set( // cal에 유저가 선택한 date와 time 저장, 초단위는 00으로 초기화
                        datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute(),00);
                if(cal.compareTo(current)<=0)
                    Toast.makeText(getContext(),"Invalid Entry",Toast.LENGTH_LONG).show();
                else{
                    MainActivity ma = (MainActivity)getActivity();
                    String k = editText.getText().toString(); // 라벨이름
                    databaseReference.child("Alarm").child(k).setValue((ad = new Obj_alarm(k, ma.alarmId)));
                    Log.e("알람", "등록..." + ma.alarmId);

                    ContentValues cv = new ContentValues();
                    cv.put("task",k);
                    obj.myInsert(cv);

                    Tab_alarm F_alarm = new Tab_alarm();
                    getFragmentManager().beginTransaction().replace(R.id.container,F_alarm).commit();

                    ma.setAlarm(cal, ma.alarmId);
                    ma.hm.put(k, new Integer(ma.alarmId));
                    ma.alarmId++;
                }
            }
        });
        return rootView;
    }
}