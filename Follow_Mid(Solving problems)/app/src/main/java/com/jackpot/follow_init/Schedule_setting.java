package com.jackpot.follow_init;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.Serializable;


/**
 * Created by KWAK on 2018-05-23.
 */

public class Schedule_setting extends AppCompatActivity{
    EditText inEvent, inDept, inDest;
    TimePicker startTime, endTime;

    int weekday;               // weekday is called to number on this app.
    Double dept_lati = 0.0, dept_longi = 0.0;
    Double dest_lati = 0.0, dest_longi = 0.0;

    public Schedule_setting() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_setting);

        // XML의 widget에 접근하기 위한 구체화.
        inEvent = findViewById(R.id.inEvent);
        inDept = findViewById(R.id.inDept);
        inDest = findViewById(R.id.inDest);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);

        Button checkMon = findViewById(R.id.btnMon);
        Button checkTue = findViewById(R.id.btnTue);
        Button checkWed = findViewById(R.id.btnWed);
        Button checkThu = findViewById(R.id.btnThu);
        Button checkFri = findViewById(R.id.btnFri);
        // 요일 숫자 변경 (월요일 -> 1 로 시작)
        checkMon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                weekday = 1;
            }
        });
        checkTue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                weekday = 2;
            }
        });
        checkWed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                weekday = 3;
            }
        });
        checkThu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                weekday = 4;
            }
        });
        checkFri.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                weekday = 5;
            }
        });

        Button btnDept = findViewById(R.id.btnDept);
        Button btnDest = findViewById(R.id.btnDest);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnSave = findViewById(R.id.btnSave);

        btnDept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), Map.class), 1);
            }
        });

        btnDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), Map.class), 2);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // Schedule 정보를 디비에 다이렉트로 꽂아넣기.
                // 위 경도 값이 return 되었을 때 save 되도록. 안그러면 DB에 제대로 값 안들어가니까.
                if(dept_lati != 0.0 && dest_longi != 0.0) {
                    ContentValues schedule_setting = new ContentValues();

                    schedule_setting.put("event_name", inEvent.getText().toString());
                    schedule_setting.put("dept_name", inDept.getText().toString());
                    schedule_setting.put("dest_name", inDest.getText().toString());

                    schedule_setting.put("weekday", weekday);
                    schedule_setting.put("str_hour", startTime.getHour());
                    schedule_setting.put("str_min", startTime.getMinute());
                    schedule_setting.put("end_hour", endTime.getHour());
                    schedule_setting.put("end_min", endTime.getMinute());

                    schedule_setting.put("dept_lati", dept_lati);
                    schedule_setting.put("dept_long", dept_longi);
                    schedule_setting.put("dest_lati", dest_lati);
                    schedule_setting.put("dest_long", dest_longi);

                    Database_overall DB_helper = Database_overall.getInstance(getApplicationContext());
                    DB_helper.Insert(schedule_setting);
                    setResult(RESULT_OK);

                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"You didn't put all of data",Toast.LENGTH_SHORT).show();
                    // 뭘 실행 또는 처리해줘야 하지?
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == -1){       // Check the result code from map activity whether it is Result_OK or Result_Cancel first.
            if(requestCode == 1){   // Request code == 1 means departure part on Schedule_setting.
                String name = data.getStringExtra("Name");
                inDept.setText(name);
                dept_lati = data.getDoubleExtra("Latitude", 0);
                dept_longi = data.getDoubleExtra("Longitude", 0);
            }

            if(requestCode == 2){   // Request code == 2 means destination part on Schedule_setting.
                String name = data.getStringExtra("Name");
                inDest.setText(name);
                dest_lati = data.getDoubleExtra("Latitude", 0);
                dest_longi = data.getDoubleExtra("Longitude", 0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
