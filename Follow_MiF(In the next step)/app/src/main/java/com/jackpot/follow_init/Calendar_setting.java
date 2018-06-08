package com.jackpot.follow_init;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import static java.security.AccessController.getContext;

/**
 * Created by ockkj on 2018-06-07.
 */

public class Calendar_setting extends AppCompatActivity{
    EditText inEvent, inDept, inDest;

    Double dept_lati = 0.0, dept_longi = 0.0;
    Double dest_lati = 0.0, dest_longi = 0.0;

    int id=0;
    int start_hour, start_minute;
    int end_hour, end_minute;

    int alarm_check = 0;

    public Calendar_setting() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_setting);

        // XML의 widget에 접근하기 위한 구체화.
        inEvent = findViewById(R.id.inEvent);
        inDept = findViewById(R.id.inDept);
        inDest = findViewById(R.id.inDest);
        /*startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);*/

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        Toast.makeText(getApplicationContext(),"Year : "+bundle.getInt("year")+"\nday : "+bundle.getInt("day"),Toast.LENGTH_LONG).show();


        ImageButton btnDept = findViewById(R.id.btnDept);
        ImageButton btnDest = findViewById(R.id.btnDest);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnStrTime = findViewById(R.id.btnStrTime);
        Button btnEndTime = findViewById(R.id.btnEndTime);

        TextView year = findViewById(R.id.year);
        TextView month = findViewById(R.id.month);
        TextView day = findViewById(R.id.day);

        year.setText(String.valueOf(bundle.getInt("year")));
        month.setText(String.valueOf(bundle.getInt("month")+1));
        day.setText(String.valueOf(bundle.getInt("day")));



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
                alarm_check = 0;
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // Schedule 정보를 디비에 다이렉트로 꽂아넣기.
                // 위 경도 값이 return 되었을 때 save 되도록. 안그러면 DB에 제대로 값 안들어가니까.
                if (dept_lati != 0.0 && dest_longi != 0.0 && (start_hour < end_hour || (start_hour == end_hour && start_minute < end_minute))) {
                    Toast.makeText(getApplicationContext(), String.valueOf(alarm_check), Toast.LENGTH_LONG).show();
                    ContentValues schedule_setting = new ContentValues();

                    schedule_setting.put("year", bundle.getInt("year"));
                    schedule_setting.put("month", bundle.getInt("month"));
                    schedule_setting.put("day", bundle.getInt("day"));

                    schedule_setting.put("event_name", inEvent.getText().toString());
                    schedule_setting.put("dept_name", inDept.getText().toString());
                    schedule_setting.put("dest_name", inDest.getText().toString());

                    schedule_setting.put("str_hour", start_hour);
                    schedule_setting.put("str_min", start_minute);
                    schedule_setting.put("end_hour", end_hour);
                    schedule_setting.put("end_min", end_minute);

                    schedule_setting.put("dept_lati", dept_lati);
                    schedule_setting.put("dept_long", dept_longi);
                    schedule_setting.put("dest_lati", dest_lati);
                    schedule_setting.put("dest_long", dest_longi);

                    schedule_setting.put("ala_code", alarm_check);

                    Database_overall DB_helper = Database_overall.getInstance(getApplicationContext());
                    DB_helper.Insert(schedule_setting);
                    setResult(RESULT_OK);

                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "You didn't put all of data or invalid data", Toast.LENGTH_SHORT).show();
                    // 뭘 실행 또는 처리해줘야 하지?
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1) {       // Check the result code from map activity whether it is Result_OK or Result_Cancel first.
            if (requestCode == 1) {   // Request code == 1 means departure part on Schedule_setting.
                String name = data.getStringExtra("Name");
                inDept.setText(name);
                dept_lati = data.getDoubleExtra("Latitude", 0);
                dept_longi = data.getDoubleExtra("Longitude", 0);
            }

            if (requestCode == 2) {   // Request code == 2 means destination part on Schedule_setting.
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
        return new TimePickerDialog(this,mTimeSetListener,start_hour,start_minute,true);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}