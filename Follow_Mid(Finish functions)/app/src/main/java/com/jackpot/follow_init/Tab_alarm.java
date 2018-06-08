package com.jackpot.follow_init;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jackpot.follow_init.R;

import static android.media.CamcorderProfile.get;

/**
 * Created by KWAK on 2018-05-14.
 */

public class Tab_alarm extends Fragment {
    private ViewGroup rootView;
    private ListView AlarmList;
    private MainActivity mainActivity;

    private ArrayList<String> qw;

    private String[] alarmList;         // It stores event_name which has alarm_code 1 from DB and it will be delivered to adapter.
    private ArrayList<Integer> idList = new ArrayList<>();  // idList contains id which is primary key of DB and have same variable "count" for matching with alarmList.
    private ArrayAdapter<String> aa;

    SQLiteDatabase database;            // Set a SQLite database
    //Database_overall DB_helper;      // Call SQLite helper which was over ridden.

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.tab_alarm, container, false);
        AlarmList = (ListView) rootView.findViewById(R.id.AlarmList);
        mainActivity = (MainActivity) getActivity();

        //DB_helper = Database_overall.getInstance(getContext());

        FloatingActionButton btnAdd = (FloatingActionButton) rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btnAdd)
                    startActivityForResult(new Intent(getContext(), Alarm_setting.class), 1);
            }
        });

        AlarmList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        AlarmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String k = (String) parent.getItemAtPosition(position);

                //DB_helper.Delete(String.valueOf(idList.get(position)));
                mainActivity.DB_helper.Delete(String.valueOf(idList.get(position)));

                Toast.makeText(rootView.getContext(), k + " Removed", Toast.LENGTH_SHORT).show();
                deleteAlarm(idList.get(position));
                refresh();

                return false;
            }
        });
/*
        //Cursor cursor = DB_helper.Select();
        Cursor cursor = mainActivity.DB_helper.Select();

        //cursor.moveToFirst();
        alarmList = new String[cursor.getCount()];      // Initiate the size of String array which stores lists of alarm have alarm code 1 in DB.
        int count = 0;                                  // count uses for index of alarmList from the scratch.
        // idList contains id which is primary key of DB

        for(int i = 0; i <alarmList.length; i++)
            alarmList[i] = "";

        while (cursor.moveToNext()) {                   // and have same variable "count" for matching with alarmList.
            if (cursor.getInt(cursor.getColumnIndex("ala_code")) == 1) {
                   if (cursor.getInt(cursor.getColumnIndex("year")) == 0) {  // 스케쥴이랑 캘린더랑 디비 값 구분을 위해.
                       // weekday 를 숫자로 박아놈.
                       alarmList[count] = cursor.getString(cursor.getColumnIndex("event_name")) + "\n" + String.valueOf(cursor.getInt(cursor.getColumnIndex("weekday"))) + "요일";
                       qw.add(cursor.getString(cursor.getColumnIndex("event_name")) + "\n" + String.valueOf(cursor.getInt(cursor.getColumnIndex("weekday"))) + "요일");
                       idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                       count++;
                   } else {     // 캘린더 + 수동
                       alarmList[count] = cursor.getString(cursor.getColumnIndex("event_name")) + "\n" + String.valueOf(cursor.getInt(cursor.getColumnIndex("day"))) + "일";
                       qw.add(cursor.getString(cursor.getColumnIndex("event_name")) + "\n" + String.valueOf(cursor.getInt(cursor.getColumnIndex("weekday"))) + "요일");
                       idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                       count++;
                   }
               }
       }
       cursor.close();

        // This conditional is for separating between alarm list has something to show or not, using number of count.
        if (count != 0) {
            //ArrayAdapter<String> aa = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, alarmList);
            //aa = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, alarmList);
            aa = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, qw);
            AlarmList.setAdapter(aa);
        }
*/
        Cursor cursor = mainActivity.DB_helper.Select();
        qw = new ArrayList<String>();
        int count = 0;
        while (cursor.moveToNext()) {                   // and have same variable "count" for matching with alarmList.
            if (cursor.getInt(cursor.getColumnIndex("ala_code")) == 1) {
                if (cursor.getInt(cursor.getColumnIndex("year")) == 0) {  // 스케쥴이랑 캘린더랑 디비 값 구분을 위해.
                    // weekday 를 숫자로 박아놈.
                    qw.add(cursor.getString(cursor.getColumnIndex("event_name")) + "\n" + String.valueOf(cursor.getInt(cursor.getColumnIndex("weekday"))) + "요일");
                    idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                    count++;
                } else {     // 캘린더 + 수동
                    qw.add(cursor.getString(cursor.getColumnIndex("event_name")) + "\n" + String.valueOf(cursor.getInt(cursor.getColumnIndex("weekday"))) + "요일");
                    idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                    count++;
                }
            }
        }
        cursor.close();

        if (count != 0) {
            aa = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, qw);
            AlarmList.setAdapter(aa);
        }

        return rootView;
    }

    // Manual alarm setting.
    public void setAlarm(Calendar target_call, int alarmId) {
        Toast.makeText(getActivity(), "Alarm is set @ " + target_call.getTime(), Toast.LENGTH_LONG).show();
        Intent callReceiver = new Intent(getContext(), Alarm_receiver.class);

        callReceiver.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callReceiver.putExtra("id", alarmId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), alarmId, callReceiver, 0);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, target_call.getTimeInMillis(), pendingIntent);

        /*
       // MainActivity ma = (MainActivity)getActivity();
        //Cursor c = ma.DB_helper.Select();
        Cursor cursor = mainActivity.DB_helper.Select();
        //c.moveToFirst();
        alarmList = new String[cursor.getCount()];
        int count = 0;

        while (cursor.moveToNext()) {                   // and have same variable "count" for matching with alarmList.
            if (cursor.getInt(cursor.getColumnIndex("ala_code")) == 1) {
                // 캘린더 + 수동
                alarmList[count] = cursor.getString(cursor.getColumnIndex("event_name")) + "\n" + String.valueOf(cursor.getInt(cursor.getColumnIndex("day"))) + "일";
                idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                count++;
            }
        }
        cursor.close();

        if (count != 0) {
            ArrayAdapter<String> aa = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, alarmList);
            AlarmList.setAdapter(aa);
        }*/
        setList();
    }

    // Auto setting alarm for schedule.
    public void setAlarm(Context context, int alarmId) {
        Calendar cal = Calendar.getInstance();
        double sec_time = 0.0;
        //Database_overall db = Database_overall.getInstance(context.getApplicationContext());
        //Cursor c = db.Select();
        Cursor cursor = mainActivity.DB_helper.Select();
        cursor.moveToLast();
        sec_time = cursor.getDouble(cursor.getColumnIndex("sec_time"));

        double sec_hour = sec_time / 60, sec_minute = sec_time % 60;
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK); // 1.. 일요일, 2.. 월요일 , ... , 7.. 토요일
        int year = cal.get(Calendar.YEAR); // 2018
        int month = cal.get(Calendar.MONTH); // 0.. 일월, 1.. 이월, ~ , 11..십이월
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // weekday 1..월요일 ~ 5..금요일
        switch (day_of_week) {
            case 1: // 일요일에
                if (cursor.getInt(cursor.getColumnIndex("weekday")) == 1) { //월요일 세팅
                    day = day + 1;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 2) { //화요일 세팅
                    day = day + 2;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 3) { //수요일 세팅
                    day = day + 3;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 4) { //목요일 세팅
                    day = day + 4;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 5) { //금요일 세팅
                    day = day + 5;
                }
                break;
            case 2: // 월요일에
                if (cursor.getInt(cursor.getColumnIndex("weekday")) == 1) { //월요일 세팅
                    day = day + 7;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 2) { //화요일 세팅
                    day = day + 1;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 3) { //수요일 세팅
                    day = day + 2;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 4) { //목요일 세팅
                    day = day + 3;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 5) { //금요일 세팅
                    day = day + 4;
                }
                break;
            case 3: // 화요일에
                if (cursor.getInt(cursor.getColumnIndex("weekday")) == 1) { //월요일 세팅
                    day = day + 6;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 2) { //화요일 세팅
                    day = day + 7;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 3) { //수요일 세팅
                    day = day + 1;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 4) { //목요일 세팅
                    day = day + 2;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 5) { //금요일 세팅
                    day = day + 3;
                }
                break;
            case 4: // 수요일에
                if (cursor.getInt(cursor.getColumnIndex("weekday")) == 1) { //월요일 세팅
                    day = day + 5;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 2) { //화요일 세팅
                    day = day + 6;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 3) { //수요일 세팅
                    day = day + 7;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 4) { //목요일 세팅
                    day = day + 1;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 5) { //금요일 세팅
                    day = day + 2;
                }
                break;
            case 5: // 목요일에
                if (cursor.getInt(cursor.getColumnIndex("weekday")) == 1) { //월요일 세팅
                    day = day + 4;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 2) { //화요일 세팅
                    day = day + 5;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 3) { //수요일 세팅
                    day = day + 6;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 4) { //목요일 세팅
                    day = day + 7;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 5) { //금요일 세팅
                    day = day + 1;
                }
                break;
            case 6: // 금요일에
                if (cursor.getInt(cursor.getColumnIndex("weekday")) == 1) { //월요일 세팅
                    day = day + 3;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 2) { //화요일 세팅
                    day = day + 4;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 3) { //수요일 세팅
                    day = day + 5;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 4) { //목요일 세팅
                    day = day + 6;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 5) { //금요일 세팅
                    day = day;
                }
                break;
            case 7: // 토요일에
                if (cursor.getInt(cursor.getColumnIndex("weekday")) == 1) { //월요일 세팅
                    day = day + 2;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 2) { //화요일 세팅
                    day = day + 3;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 3) { //수요일 세팅
                    day = day + 4;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 4) { //목요일 세팅
                    day = day + 5;
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 5) { //금요일 세팅
                    day = day + 6;
                }
                break;
            default:
                Log.e("에러", "요일 체크 에러");
        }

        cal.set(year, month, day,
                (cursor.getInt(cursor.getColumnIndex("str_hour")) - (int) sec_hour),
                (cursor.getInt(cursor.getColumnIndex("str_min")) - (int) sec_minute), 00);

        Toast.makeText(context, "Alarm is set @ " + cal.getTime(), Toast.LENGTH_LONG).show();
        Intent callReceiver = new Intent(context, Alarm_receiver.class);
        callReceiver.putExtra("id", alarmId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, callReceiver, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

        cursor.close();

//        Cursor cursor = mainActivity.DB_helper.Select();
/*
        cursor.moveToFirst();
        alarmList = new String[cursor.getCount()];

        int count = 0;
        if (cursor.getCount() == 1) {
            alarmList[count] = cursor.getString(cursor.getColumnIndex("event_name")) + "\n" + cal.getTime();
            idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
            count++;
        }
        else {
            boolean isFirst = true;

            while (cursor.moveToNext()) {
                if (isFirst) {
                    cursor.moveToFirst();
                    isFirst = false;
                }
                if (cursor.getInt(cursor.getColumnIndex("ala_code")) == 1) {
                    alarmList[count] = cursor.getString(cursor.getColumnIndex("event_name")) + "\n" + cal.getTime();
                    idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                    count++;
                }
            }
        }
        cursor.close();

        if (count != 0) {
            ArrayAdapter<String> aa = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_checked, alarmList);
            AlarmList.setAdapter(aa);
        }*/
        setList();
    }

    // Auto setting alarm for calendar.
    public void setAlarm(int alarmId, Context context) {
        Calendar cal = Calendar.getInstance();
        double sec_time = 0.0;
        //Database_overall db = Database_overall.getInstance(context.getApplicationContext());
        //Cursor c = db.Select();
        Cursor cursor = mainActivity.DB_helper.Select();
        cursor.moveToLast();
        sec_time = cursor.getDouble(cursor.getColumnIndex("sec_time"));

        double sec_hour = sec_time / 60, sec_minute = sec_time % 60;
        int year = cursor.getInt(cursor.getColumnIndex("year"));  // 2018
        int month = cursor.getInt(cursor.getColumnIndex("month")) + 1; // 0.. 일월, 1.. 이월, ~ , 11..십이월
        int day = cursor.getInt(cursor.getColumnIndex("day"));

        cal.set(year, month, day,
                (cursor.getInt(cursor.getColumnIndex("str_hour")) - (int) sec_hour),
                (cursor.getInt(cursor.getColumnIndex("str_min")) - (int) sec_minute), 00);

        Toast.makeText(context, "Alarm is set @ " + cal.getTime(), Toast.LENGTH_LONG).show();
        //MainActivity ma = (MainActivity)getActivity();
        Intent callReceiver = new Intent(context, Alarm_receiver.class);
        callReceiver.putExtra("id", alarmId);
//        callReceiver.putExtra("shortest",ma.shortest);
        //    callReceiver.putExtra("shortest",mainActivity.shortest);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, callReceiver, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

/*
        c.moveToFirst();
        alarmList = new String[c.getCount()];
        Log.e("되니", "getCount(): " + c.getCount());

        int count = 0;
        if (c.getCount() == 1) {
            alarmList[count] = c.getString(c.getColumnIndex("event_name")) + "\n" + cal.getTime();
            idList.add(count, c.getInt(c.getColumnIndex("_id")));
            count++;
        }
        else {
            boolean isFirst = true;

            while (c.moveToNext()) {
                if (isFirst) {
                    c.moveToFirst();
                    isFirst = false;
                }
                if (c.getInt(c.getColumnIndex("ala_code")) == 1) {
                    alarmList[count] = c.getString(c.getColumnIndex("event_name")) + "\n" + cal.getTime();
                    idList.add(count, c.getInt(c.getColumnIndex("_id")));
                    count++;
                }
            }
        }
        c.close();

        if (count != 0) {
            ArrayAdapter<String> aa = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_checked, alarmList);
            AlarmList.setAdapter(aa);
        }*/
        setList();
    }


    // 디비 훑고 리스트 셋팅해주는 메소드 만들기.
    public void setList() {
        Cursor cursor = mainActivity.DB_helper.Select();
        qw = new ArrayList<String>();
        int count = 0;
        while (cursor.moveToNext()) {                   // and have same variable "count" for matching with alarmList.
            if (cursor.getInt(cursor.getColumnIndex("ala_code")) == 1) {
                if (cursor.getInt(cursor.getColumnIndex("year")) == 0) {  // 스케쥴이랑 캘린더랑 디비 값 구분을 위해.
                    // weekday 를 숫자로 박아놈.
                    qw.add(cursor.getString(cursor.getColumnIndex("event_name")) + "\n" + String.valueOf(cursor.getInt(cursor.getColumnIndex("weekday"))) + "요일");
                    idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                    count++;
                } else {     // 캘린더 + 수동
                    qw.add(cursor.getString(cursor.getColumnIndex("event_name")) + "\n" + String.valueOf(cursor.getInt(cursor.getColumnIndex("weekday"))) + "요일");
                    idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                    count++;
                }
            }
        }
        cursor.close();

        if (count != 0) {
            aa = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, qw);
            AlarmList.setAdapter(aa);
        }

    }

    // 프래그먼트 갱신
    public void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public void deleteAlarm(int alarmId) {
        Intent callReceiver = new Intent(getContext(), Alarm_receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), alarmId, callReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // result code == -1 is signal from alarmSetting activity after set some input data in alarmSetting and then call this activity to set alarm.
        if (resultCode == -1) {
            Calendar ex = (Calendar) data.getExtras().getSerializable("Calendar");
            int hour = ex.get(Calendar.HOUR_OF_DAY);
            int minute = ex.get(Calendar.MINUTE);

            ContentValues values = new ContentValues();
            values.put("event_name", data.getStringExtra("eventName"));
            values.put("ala_hour", hour);
            values.put("ala_min", minute);
            values.put("ala_code", 1);
            //MainActivity ma = (MainActivity) getActivity();
            //ma.DB_helper.Insert(values);

            mainActivity.DB_helper.Insert(values);
//            Cursor c = ma.DB_helper.Select();
            Cursor cursor = mainActivity.DB_helper.Select();
            cursor.moveToLast();
            setAlarm((Calendar) data.getExtras().getSerializable("Calendar"), cursor.getInt(cursor.getColumnIndex("_id")));
            cursor.close();
        }
    }
}