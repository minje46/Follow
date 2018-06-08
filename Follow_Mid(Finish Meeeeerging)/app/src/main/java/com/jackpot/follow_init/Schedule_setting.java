package com.jackpot.follow_init;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.Serializable;


/**
 * Created by KWAK on 2018-05-23.
 */

public class Schedule_setting extends AppCompatActivity{
    EditText inEvent, inDept, inDest;

    int weekday;               // weekday is called to number on this app.
    int check_weekday = 0;
    int id = 0;
    int start_hour, start_minute;
    int end_hour, end_minute;
    int alarm_check = 0;

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

        final CheckBox checkMon = findViewById(R.id.btnMon);
        final CheckBox checkTue = findViewById(R.id.btnTue);
        final CheckBox checkWed = findViewById(R.id.btnWed);
        final CheckBox checkThu = findViewById(R.id.btnThu);
        final CheckBox checkFri = findViewById(R.id.btnFri);
        // 요일 숫자 변경 (월요일 -> 1 로 시작)
        checkMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkTue.isChecked() || checkWed.isChecked() || checkThu.isChecked() || checkFri.isChecked())
                    check_weekday = 1;
                weekday = 1;
            }
        });
        checkTue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkMon.isChecked() || checkWed.isChecked() || checkThu.isChecked() || checkFri.isChecked())
                    check_weekday = 1;
                weekday = 2;
            }
        });
        checkWed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkTue.isChecked() || checkMon.isChecked() || checkThu.isChecked() || checkFri.isChecked())
                    check_weekday = 1;
                weekday = 3;
            }
        });
        checkThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkTue.isChecked() || checkWed.isChecked() || checkMon.isChecked() || checkFri.isChecked())
                    check_weekday = 1;
                weekday = 4;
            }
        });
        checkFri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkTue.isChecked() || checkWed.isChecked() || checkThu.isChecked() || checkMon.isChecked())
                    check_weekday = 1;
                weekday = 5;
            }
        });

        ImageButton btnDept = findViewById(R.id.btnDept);
        ImageButton btnDest = findViewById(R.id.btnDest);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnStrTime = findViewById(R.id.btnStrTime);
        Button btnEndTime = findViewById(R.id.btnEndTime);

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

        // 시간 선택 다이얼로그
        btnStrTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = 0;
                showDialog(id);
            }
            private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                }
            };
        });

        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = 1;
                showDialog(id);
            }
            private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                }
            };
        });

        Button checkBox = findViewById(R.id.checkAlarm);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm_check = 1;
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
                if (dept_lati != 0.0 && dest_longi != 0.0 && start_hour >=7 && end_hour <= 22 && (start_hour < end_hour || (start_hour == end_hour && start_minute < end_minute)) && check_weekday ==0) {
                    Database_overall DB_helper = Database_overall.getInstance(getApplicationContext());
                    ContentValues schedule_setting = new ContentValues();

                    Cursor cursor = DB_helper.Select();
                    if(cursor != null) {
                        while (cursor.moveToNext()) {
                            if ((start_hour >= cursor.getInt(cursor.getColumnIndex("str_hour")) && start_hour <= cursor.getInt(cursor.getColumnIndex("end_hour")) ||
                                    (end_hour >= cursor.getInt(cursor.getColumnIndex("str_hour")) && end_hour <= cursor.getInt(cursor.getColumnIndex("end_hour"))) && weekday == cursor.getInt(cursor.getColumnIndex("weekday")))) {
                                Toast.makeText(getApplicationContext(), "일정이 겹칩니다!!!", Toast.LENGTH_LONG).show();
                            } else {
                                schedule_setting.put("event_name", inEvent.getText().toString());
                                schedule_setting.put("dept_name", inDept.getText().toString());
                                schedule_setting.put("dest_name", inDest.getText().toString());

                                schedule_setting.put("weekday", weekday);
                                schedule_setting.put("str_hour", start_hour);
                                schedule_setting.put("str_min", start_minute);
                                schedule_setting.put("end_hour", end_hour);
                                schedule_setting.put("end_min", end_minute);

                                schedule_setting.put("dept_lati", dept_lati);
                                schedule_setting.put("dept_long", dept_longi);
                                schedule_setting.put("dest_lati", dest_lati);
                                schedule_setting.put("dest_long", dest_longi);

                                schedule_setting.put("ala_code", alarm_check);


                                DB_helper.Insert(schedule_setting);
                                setResult(RESULT_OK);

                                finish();
                            }
                        }
                    }else {
                        Log.d("null", "null 이면 일로 와야함");
                        schedule_setting.put("event_name", inEvent.getText().toString());
                        schedule_setting.put("dept_name", inDept.getText().toString());
                        schedule_setting.put("dest_name", inDest.getText().toString());

                        schedule_setting.put("weekday", weekday);
                        schedule_setting.put("str_hour", start_hour);
                        schedule_setting.put("str_min", start_minute);
                        schedule_setting.put("end_hour", end_hour);
                        schedule_setting.put("end_min", end_minute);

                        schedule_setting.put("dept_lati", dept_lati);
                        schedule_setting.put("dept_long", dept_longi);
                        schedule_setting.put("dest_lati", dest_lati);
                        schedule_setting.put("dest_long", dest_longi);

                        schedule_setting.put("ala_code", alarm_check);


                        DB_helper.Insert(schedule_setting);
                        setResult(RESULT_OK);

                        finish();
                    }

                } else if (check_weekday == 1){
                    Toast.makeText(getApplicationContext(), "You didn't put all of data or invalid data", Toast.LENGTH_SHORT).show();
                    check_weekday = 0;
                    checkMon.setChecked(false);
                    checkTue.setChecked(false);
                    checkWed.setChecked(false);
                    checkThu.setChecked(false);
                    checkFri.setChecked(false);
                    // 뭘 실행 또는 처리해줘야 하지?
                }
                else
                    Toast.makeText(getApplicationContext(), "일정이 겹칩니다!", Toast.LENGTH_SHORT).show();
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
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
            TextView strAP = findViewById(R.id.strTimeTextAP);
            TextView strHour = findViewById(R.id.strTimeTextHour);
            TextView strMinute = findViewById(R.id.strTimeTextMinute);
            TextView endAP = findViewById(R.id.endTimeTextAP);
            TextView endHour = findViewById(R.id.endTimeTextHour);
            TextView endMinute = findViewById(R.id.endTimeTextMinute);

            if(id == 0) {
                start_hour = hourOfDay;
                start_minute = min;

                if(start_hour >= 12) strAP.setText("오후");
                else if(start_hour < 12) strAP.setText("오전");

                strHour.setText(String.valueOf(start_hour));
                strMinute.setText(String.valueOf(start_minute));

                Toast.makeText(getApplicationContext(),
                        "시간 : " + start_hour + " : " + start_minute, Toast.LENGTH_SHORT).show();

            }
            else{
                end_hour = hourOfDay;
                end_minute = min;

                if(end_hour >= 12) endAP.setText("오후");
                else if(end_hour < 12) endAP.setText("오전");

                endHour.setText(String.valueOf(end_hour));
                endMinute.setText(String.valueOf(end_minute));
                Toast.makeText(getApplicationContext(),"시간 : " + end_hour + " : " + end_minute, Toast.LENGTH_SHORT).show();

            }
        }
    };
    @Override
    protected Dialog onCreateDialog(int temp_id){
        return new TimePickerDialog(this,TimePickerDialog.THEME_DEVICE_DEFAULT_DARK, mTimeSetListener,start_hour,start_minute,true);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
