/*
package com.jackpot.follow_init;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class AutoAlarmService extends Service {
    DatePicker datePicker;
    TimePicker timePicker;
    Button b1;
    EditText editText;
    AlarmData ad;
    Data obj;
    ArrayList<String> items;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    public AutoAlarmService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        datePicker=(DatePicker)rootView.findViewById(R.id.datePicker);
        timePicker=(TimePicker)rootView.findViewById(R.id.timePicker);
        b1=(Button)rootView.findViewById(R.id.button2);
        editText = (EditText)rootView.findViewById(R.id.editText);
        items = new ArrayList<String>();
        obj = new Data(rootView.getContext());

        Calendar now=Calendar.getInstance();
        datePicker.init(
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH),null
        );
        timePicker.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));

        Log.e("캘린더", "Hour of day: " + Calendar.YEAR);
        Log.e("캘린더", "Hour of day: " + Calendar.MONTH);
        Log.e("캘린더", "Hour of day: " + Calendar.DAY_OF_MONTH);
        Log.e("캘린더", "Hour of day: " + Calendar.HOUR_OF_DAY);
        Log.e("캘린더", "Hour of day: " + Calendar.MINUTE);

        timePicker.setCurrentMinute(now.get(Calendar.MINUTE));
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar current=Calendar.getInstance();
                Calendar cal=Calendar.getInstance();
                cal.set(
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
                    databaseReference.child("Alarm").child(k).setValue((ad = new AlarmData(k, ma.alarmId)));
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

*/