package com.jackpot.follow_init;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

/**
 * Created by KWAK on 2018-05-14.
 */

public class Tab_alarm extends Fragment {
    SQLiteDatabase database;            // Set a SQLite database
    Database_overall db;      // Call SQLite helper which was over ridden.

    String[] alarmList;                 // 데이테베이스에서 이벤트 이름 빼오는 것. 어레이 어댑터에 넘기는 용도.(알람 리스트)
    ArrayList<String> al = new ArrayList<String>();         // Set an array list which store alarms.
    ArrayList<String> ide = new ArrayList<>();         // Set an array list which store id of alarms can discrete others.
    ArrayList<Integer> idList = new ArrayList<>();
    ListView AlarmList;
    ViewGroup rootView;
    Obj_schedule fromThread;

    // Use Firebase.
    //private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    //private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        rootView =(ViewGroup)inflater.inflate(R.layout.tab_alarm, container, false);
        AlarmList = (ListView)rootView.findViewById(R.id.AlarmList);

        db = Database_overall.getInstance(getContext());

        FloatingActionButton btnAdd = (FloatingActionButton)rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Activity alarm call.
                if (v.getId() == R.id.btnAdd)
                    startActivityForResult(new Intent(getContext(), Alarm_setting.class), 1);
            }
        });

        //알람 리스트 롱클릭해서 딜리트
        AlarmList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        AlarmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String k = (String)parent.getItemAtPosition(position);
                //db.update(String.valueOf(idList.get(position)));
                db.delete(String.valueOf(idList.get(position)));
                // sql 디비
                /*
                database = database_alarm.getWritableDatabase();
                database.delete(Database_alarm.TABLE_TASK, ide.get(position) + "=" + Database_alarm.KEY_ID, null);
                */
                Toast.makeText(rootView.getContext(), k + " Removed", Toast.LENGTH_SHORT).show();
                deleteAlarm(idList.get(position));
                refresh();
                Log.e("알람", "삭제..." + idList.get(position));

                return false;
            }
        });

        Cursor c = db.Select();
        c.moveToFirst();
        alarmList = new String[c.getCount()];
        int count = 0;
        if (c.getCount() == 1) {
            alarmList[count] = c.getString(c.getColumnIndex("event_name"));
            idList.add(0, c.getInt(c.getColumnIndex("_id")));
            count++;
        } else {
            boolean isFirst = true;

            while (c.moveToNext()) {
                if (isFirst) {
                    c.moveToFirst();
                    isFirst = false;
                }
                if (c.getInt(c.getColumnIndex("ala_code")) == 1) {
                    alarmList[count] = c.getString(c.getColumnIndex("event_name"));
                    idList.add(count, c.getInt(c.getColumnIndex("_id")));
                    count++;
                }
            }
        }
        c.close();

        if (count != 0) {
            ArrayAdapter<String> aa = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_checked, alarmList);
            AlarmList.setAdapter(aa);
        }

        return rootView;
    }

    // 프래그먼트 갱신
    private void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    // 자동 알람.
    public void setAlarm(int alarmId, Context context) {
        Log.e("데이터in자동알람", "진입 됨?");
        double sec_time = 0;

        Calendar cal = Calendar.getInstance();

        Database_overall db = Database_overall.getInstance(context.getApplicationContext());
        Cursor c = db.Select();
        c.moveToLast();
        sec_time = c.getDouble(c.getColumnIndex("sec_time"));

        double sec_hour = sec_time / 60, sec_minute = sec_time % 60;
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK); // 1.. 일요일, 2.. 월요일 , ... , 7.. 토요일
        int year = cal.get(Calendar.YEAR); // 2018
        int month = cal.get(Calendar.MONTH); // 0.. 일월, 1.. 이월, ~ , 11..십이월
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // weekday 1..월요일 ~ 5..금요일
        switch (day_of_week) {
            case 1: // 일요일에
                if (c.getInt(c.getColumnIndex("weekday")) == 1) { //월요일 세팅
                    day = day + 1;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 2) { //화요일 세팅
                    day = day + 2;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 3) { //수요일 세팅
                    day = day + 3;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 4) { //목요일 세팅
                    day = day + 4;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 5) { //금요일 세팅
                    day = day + 5;
                }
                break;
            case 2: // 월요일에
                if (c.getInt(c.getColumnIndex("weekday")) == 1) { //월요일 세팅
                    day = day + 7;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 2) { //화요일 세팅
                    day = day + 1;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 3) { //수요일 세팅
                    day = day + 2;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 4) { //목요일 세팅
                    day = day + 3;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 5) { //금요일 세팅
                    day = day + 4;
                }
                break;
            case 3: // 화요일에
                if (c.getInt(c.getColumnIndex("weekday")) == 1) { //월요일 세팅
                    day = day + 6;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 2) { //화요일 세팅
                    day = day + 7;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 3) { //수요일 세팅
                    day = day + 1;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 4) { //목요일 세팅
                    day = day + 2;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 5) { //금요일 세팅
                    day = day + 3;
                }
                break;
            case 4: // 수요일에
                if (c.getInt(c.getColumnIndex("weekday")) == 1) { //월요일 세팅
                    day = day + 5;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 2) { //화요일 세팅
                    day = day + 6;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 3) { //수요일 세팅
                    day = day + 7;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 4) { //목요일 세팅
                    day = day + 1;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 5) { //금요일 세팅
                    day = day + 2;
                }
                 break;
            case 5: // 목요일에
                if (c.getInt(c.getColumnIndex("weekday")) == 1) { //월요일 세팅
                    day = day + 4;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 2) { //화요일 세팅
                    day = day + 5;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 3) { //수요일 세팅
                    day = day + 6;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 4) { //목요일 세팅
                    day = day + 7;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 5) { //금요일 세팅
                    day = day + 1;
                }
                 break;
            case 6: // 금요일에
                if (c.getInt(c.getColumnIndex("weekday")) == 1) { //월요일 세팅
                    day = day + 3;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 2) { //화요일 세팅
                    day = day + 4;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 3) { //수요일 세팅
                    day = day + 5;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 4) { //목요일 세팅
                    day = day + 6;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 5) { //금요일 세팅
                    day = day + 7;
                }
                 break;
            case 7: // 토요일에
                if (c.getInt(c.getColumnIndex("weekday")) == 1) { //월요일 세팅
                    day = day + 2;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 2) { //화요일 세팅
                    day = day + 3;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 3) { //수요일 세팅
                    day = day + 4;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 4) { //목요일 세팅
                    day = day + 5;
                }
                else if (c.getInt(c.getColumnIndex("weekday")) == 5) { //금요일 세팅
                    day = day + 6;
                }
                 break;
            default:
                 Log.e("자동", "요일 체크 에러");
        }


        cal.set(year,
                month,
                day,
                (c.getInt(c.getColumnIndex("str_hour")) - (int) sec_hour),
                (c.getInt(c.getColumnIndex("str_min")) - (int) sec_minute), 00);
        Log.e("자동알람", "year: " + cal.get(Calendar.YEAR) + ", month: " + cal.get(Calendar.MONTH) + ", day: " + cal.get(Calendar.DAY_OF_MONTH) + ", hour: " + cal.get(Calendar.HOUR_OF_DAY) + ", minute: " + cal.get(Calendar.MINUTE));

        Toast.makeText(context, "Alarm is set @ " + cal.getTime(), Toast.LENGTH_LONG).show();
        Intent callReceiver = new Intent(context, Alarm_receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, callReceiver, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

    // 수동 알람.
    public void setAlarm(Calendar targetcall, int alarmId){
        Toast.makeText(getActivity(),"Alarm is set @ "+targetcall.getTime(),Toast.LENGTH_LONG).show();
        Intent callReceiver = new Intent(getContext(), Alarm_receiver.class);
        callReceiver.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), alarmId, callReceiver, 0);
        AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,targetcall.getTimeInMillis(), pendingIntent);
        Log.e("알람", "추가..." + String.valueOf(alarmId));

        MainActivity ma = (MainActivity)getActivity();
        Cursor c = ma.DB_helper.Select();
        c.moveToFirst();
        alarmList = new String[c.getCount()];
        Log.e("되니", "getCount(): " + c.getCount());

        int count = 0;
        if (c.getCount() == 1) {
            alarmList[count] = c.getString(c.getColumnIndex("event_name"));
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
                    alarmList[count] = c.getString(c.getColumnIndex("event_name"));
                    idList.add(count, c.getInt(c.getColumnIndex("_id")));
                    count++;
                }
            }
        }
        c.close();

        if (count != 0) {
            ArrayAdapter<String> aa = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_checked, alarmList);
            AlarmList.setAdapter(aa);
        }
        Log.e("알람", "추가..." + String.valueOf(alarmId));
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

        // request code == 1 is signal from alarmSetting activity after set some input data in alarmSetting and then call this activity to set alarm.
        if(requestCode == 1){
            if(!data.getStringExtra("eventName").equals("")) {
     //           setAlarm((Calendar) data.getExtras().getSerializable("Calendar"), (data.getIntExtra("alarmId", 0)));

                // SQLite Database_alarm 에 Alarm data 저장.
                /*
                ContentValues alarmValue = new ContentValues();
                alarmValue.put("task", data.getStringExtra("eventName"));
                database_alarm.myInsert(alarmValue);
*/

                //To store alarm data which is event name and id in hashMap
                //MainActivity ma = (MainActivity)getActivity();
                //ma.hm.put(data.getStringExtra("eventName"), (Integer)data.getIntExtra("alarmId",0));


                Calendar ex = (Calendar)data.getExtras().getSerializable("Calendar");
                int hour = ex.get(Calendar.HOUR_OF_DAY);
                int minute = ex.get(Calendar.MINUTE);
                Log.e("되니", "hour: " + hour + "minute: " + minute);

                ContentValues values = new ContentValues();
                values.put("event_name",data.getStringExtra("eventName"));
                values.put("ala_hour", hour);
                values.put("ala_min", minute);
                values.put("ala_code", 1);
                MainActivity ma = (MainActivity)getActivity();
                ma.DB_helper.Insert(values);

                Cursor c = ma.DB_helper.Select();
                c.moveToLast();
                setAlarm((Calendar) data.getExtras().getSerializable("Calendar"), c.getInt(c.getColumnIndex("_id")));


            }
            else {} // event_name이 null일때 처리 해줘야함.
        }
    }

}
