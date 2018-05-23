package com.jackpot.follow_init;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by KWAK on 2018-05-23.
 */

public class Schedule_setting extends AppCompatActivity {
    // Database 객체 생성. (Firebase에서 schedule의 tree를 load.)
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("Schedule");

    Fragment1 F_map;                // Map Fragment 생성.
    obj_schedule data;
    public Schedule_setting(){
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_setting);

        F_map = new Fragment1();
        data = new obj_schedule();

        // XML의 widget에 접근하기 위한 구체화.
        final EditText inEvent = findViewById(R.id.inEvent);
        final EditText inDept = findViewById(R.id.inDept);
        final EditText inDest = findViewById(R.id.inDest);
        final TimePicker startTime = findViewById(R.id.startTime);
        final TimePicker endTime = findViewById(R.id.endTime);
        Button btnDept = findViewById(R.id.btnDept);
        Button btnDest = findViewById(R.id.btnDest);
        Button btnCancle = findViewById(R.id.btnCancle);
        Button btnSave = findViewById(R.id.btnSave);

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // Schedule 의 정보를 객체 생성하고 내부에 값 저장.
                //obj_schedule data = new obj_schedule();

                // 객체에 이름, 출발, 도착지 저장.
                data.setEvent_name(inEvent.getText().toString());
                //data.setDept_name(inDept.getText().toString());
                data.setDest_name(inDest.getText().toString());

                // 객체에 time 저장.
                data.setStart_hour(startTime.getHour());
                data.setStart_minute(startTime.getMinute());
                data.setEnd_hour(endTime.getHour());
                data.setEnd_mintue(endTime.getMinute());

                // 객체를 DB에 저장.
                // databaseReference.child(data.getEvent_name()).setValue(data);
                DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                dr.child("Schedule").push().setValue(data);

                Toast.makeText(getApplicationContext(), data.getEvent_name() +" schedule is saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnDept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.schedule_setting_main, F_map).commit();


                //data.setDept_name(inDept.getText().toString());
                //data.setDept_latitude();
                //data.setDept_longitude();
            }
        });

        btnDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.schedule_setting_main, F_map).commit();
            }
        });
    }

    /*
    private long getMillis(String day) {
        DateTime date = getDateTimePattern().parseDateTime(day);
        return date.getMillis();
    }

    private DateTimeFormatter getDateTimePattern() {
        return DateTimeFormat.forPattern("HH-mm");
    }
    */
}

