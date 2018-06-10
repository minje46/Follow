package com.jackpot.follow_init;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.security.AccessController.getContext;

/**
 * Created by ockkj on 2018-06-07.
 */

public class Calendar_setting extends AppCompatActivity {
    EditText inEvent, inDept, inDest;

    Double dept_lati = 0.0, dept_longi = 0.0;
    Double dest_lati = 0.0, dest_longi = 0.0;

    int id = 0;
    int start_hour, start_minute;
    int end_hour, end_minute;
    int pathType  = -1;
    int early = -1;
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

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        Button btnPreAlarm = findViewById(R.id.btnPreAlarm);
        registerForContextMenu(btnPreAlarm);

        ImageButton btnDept = findViewById(R.id.btnDept);
        ImageButton btnDest = findViewById(R.id.btnDest);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnStrTime = findViewById(R.id.btnStrTime);
        Button btnEndTime = findViewById(R.id.btnEndTime);

        // 디폴트 설정때문에
        TextView strAP = findViewById(R.id.strTimeTextAP);
        TextView strHour = findViewById(R.id.strTimeTextHour);
        TextView strMinute = findViewById(R.id.strTimeTextMinute);
        TextView endAP = findViewById(R.id.endTimeTextAP);
        TextView endHour = findViewById(R.id.endTimeTextHour);
        TextView endMinute = findViewById(R.id.endTimeTextMinute);
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat currentHour = new SimpleDateFormat("HH");
        SimpleDateFormat currentMin = new SimpleDateFormat("mm");
        if (Integer.parseInt(currentHour.format(date)) > 12) {
            strAP.setText("오후");
            endAP.setText("오후");
        } else {
            strAP.setText("오전");
            endAP.setText("오전");
        }

        strHour.setText(currentHour.format(date));
        endHour.setText(currentHour.format(date));
        strMinute.setText(currentMin.format(date));
        endMinute.setText(currentMin.format(date));

        final CheckBox checkAll = findViewById(R.id.btnWal);
        checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAll.isChecked())
                    pathType = 0;
            }
        });

        final CheckBox checkSub = findViewById(R.id.btnSub);
        checkSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkSub.isChecked()){
                    pathType = 1;
                }
            }
        });
        final CheckBox checkBus = findViewById(R.id.btnBus);
        checkBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBus.isChecked()){
                    pathType = 2;
                }
            }
        });

        TextView year = findViewById(R.id.year);
        TextView month = findViewById(R.id.month);
        TextView day = findViewById(R.id.day);

        year.setText(String.valueOf(bundle.getInt("year")));
        month.setText(String.valueOf(bundle.getInt("month") + 1));
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

        final CheckBox checkBox = findViewById(R.id.checkAlarm);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked() && checkBox.isActivated())
                    alarm_check = 1;
                else
                    alarm_check = 0;
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
                if (dept_lati != 0.0 && dest_longi != 0.0 && pathType != -1 && early != -1 && (start_hour < end_hour || (start_hour == end_hour && start_minute < end_minute))) {
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

                    schedule_setting.put("path_type",pathType);
                    schedule_setting.put("early_min",early);
                    schedule_setting.put("ala_code", alarm_check);

                    Database_overall DB_helper = Database_overall.getInstance(getApplicationContext());
                    DB_helper.Insert(schedule_setting);
                    setResult(RESULT_OK);

                    // LoadingApplication loadingApplication = new LoadingApplication();

                    progressON("Loading...");


                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressOFF();
                            finish();
                        }
                    }, 3000);
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


            if (id == 0) {
                start_hour = hourOfDay;
                start_minute = min;

                if (start_hour >= 12) strAP.setText("오후");
                else if (start_hour < 12) strAP.setText("오전");

                strHour.setText(String.valueOf(start_hour));
                strMinute.setText(String.valueOf(start_minute));
            } else {
                end_hour = hourOfDay;
                end_minute = min;

                if (end_hour >= 12) endAP.setText("오후");
                else if (end_hour < 12) endAP.setText("오전");

                endHour.setText(String.valueOf(end_hour));
                endMinute.setText(String.valueOf(end_minute));
            }
        }
    };

    @Override
    protected Dialog onCreateDialog(int temp_id) {
        return new TimePickerDialog(this, mTimeSetListener, start_hour, start_minute, true);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    public void progressON(Activity activity, String str) {
        LoadingApplication.getInstance().progressON(activity, str);
    }

    public void progressON(String message) {
        LoadingApplication.getInstance().progressON(this, message);
    }

    public void progressOFF() {
        LoadingApplication.getInstance().progressOFF();
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("몇분 전에?");
        menu.add(0, 1, 0, "0분");
        menu.add(0, 2, 0, "10분");
        menu.add(0, 3, 0, "20분");
        menu.add(0, 4, 0, "30분");
        menu.add(0, 5, 0, "40분");
        menu.add(0, 6, 0, "50분");
        menu.add(0, 7, 0, "60분");
        menu.add(0, 8, 0, "70분");
        menu.add(0, 9, 0, "80분");
        menu.add(0, 10, 0, "90분");
    }

    public boolean onContextItemSelected(MenuItem item) {
        TextView textView = findViewById(R.id.preAlarmMinute);
        switch (item.getItemId()) {
            case 1:
                textView.setText("0분");
                early = 0;
                return true;
            case 2:
                textView.setText("10분");
                early = 10;
                return true;
            case 3:
                textView.setText("20분");
                early = 20;
                return true;
            case 4:
                textView.setText("30분");
                early = 30;
                return true;
            case 5:
                textView.setText("40분");
                early = 40;
                return true;
            case 6:
                textView.setText("50분");
                early = 50;
                return true;
            case 7:
                textView.setText("60분");
                early = 60;
                return true;
            case 8:
                textView.setText("70분");
                early = 70;
                return true;
        }
        return true;
    }
}