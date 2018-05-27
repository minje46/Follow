package com.jackpot.follow_init;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by KWAK on 2018-05-23.
 */

public class Schedule_setting extends AppCompatActivity{
    // Database 객체 생성. (Firebase에서 schedule의 tree를 load.)
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("Schedule");

    EditText inEvent;
    EditText inDept;
    EditText inDest;
    TimePicker startTime;
    TimePicker endTime;

    obj_schedule dataObj;

    int set_code = 0;        // code 0 - set result to departure / code 1 - set result to destination.

    public Schedule_setting(){}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_setting);

        dataObj = new obj_schedule();

        // XML의 widget에 접근하기 위한 구체화.
        inEvent = findViewById(R.id.inEvent);
        inDept = findViewById(R.id.inDept);
        inDest = findViewById(R.id.inDest);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        Button btnDept = findViewById(R.id.btnDept);
        Button btnDest = findViewById(R.id.btnDest);
        Button btnCancle = findViewById(R.id.btnCancle);
        Button btnSave = findViewById(R.id.btnSave);

        btnDept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(),Map.class),1);
            }
        });

        btnDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(),Map.class),2);
            }
        });

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
                dataObj.setEvent_name(inEvent.getText().toString());
                dataObj.setDept_name(inDept.getText().toString());
                dataObj.setDest_name(inDest.getText().toString());

                // 객체에 time 저장.
                dataObj.setStart_hour(startTime.getHour());
                dataObj.setStart_minute(startTime.getMinute());
                dataObj.setEnd_hour(endTime.getHour());
                dataObj.setEnd_mintue(endTime.getMinute());

                // 객체를 DB에 저장.
                // databaseReference.child(data.getEvent_name()).setValue(data);
                DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                //dr.child("Schedule").push().setValue(data);
                Toast.makeText(getApplicationContext(), dataObj.getEvent_name() +" schedule is saved", Toast.LENGTH_SHORT).show();

                Bundle send = new Bundle();
//                send.putSerializable("FromSetting",data);
                finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(set_code == 0){
            String name = data.getStringExtra("Name");
            inDept.setText(name);
            set_code = 1;

            Double latitude = data.getDoubleExtra("Latitude", 0);
            Double longitude = data.getDoubleExtra("Longitude", 0);
            Toast.makeText(getApplicationContext(),"Name : "+name +"\nLatitude : "+latitude +"\nLongitude : "+longitude,Toast.LENGTH_SHORT).show();

            dataObj.setDept_name(name);
            dataObj.setDest_latitude(latitude);
            dataObj.setDept_longitude(longitude);
        } else {
            String name = data.getStringExtra("Name");
            inDest.setText(name);
            set_code = 0;

            Double latitude = data.getDoubleExtra("Latitude", 0);
            Double longitude = data.getDoubleExtra("Longitude", 0);
            Toast.makeText(getApplicationContext(),"Name : "+name +"\nLatitude : "+latitude +"\nLongitude : "+longitude,Toast.LENGTH_SHORT).show();

            dataObj.setDest_name(name);
            dataObj.setDest_latitude(latitude);
            dataObj.setDest_longitude(longitude);
        }
    }
}

