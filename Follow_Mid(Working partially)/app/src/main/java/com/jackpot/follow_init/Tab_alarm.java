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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
    SQLiteDatabase database;            // Set a SQLite database
    Database_alarm database_alarm;      // Call SQLite helper which was over ridden.

    String[] alarmList;                 // 데이테베이스에서 이벤트 이름 빼오는 것. 어레이 어댑터에 넘기는 용도.(알람 리스트)
    ArrayList<String> al = new ArrayList<String>();         // Set an array list which store alarms.
    ArrayList<String> ide = new ArrayList<>();         // Set an array list which store id of alarms can discrete others.
    ListView AlarmList;
    ViewGroup rootView;
    Obj_schedule fromThread;

    // Use Firebase.
    //private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    //private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
  //      final ViewGroup rootView =(ViewGroup)inflater.inflate(R.layout.tab_alarm, container, false);

  //      ListView AlarmList = (ListView)rootView.findViewById(R.id.AlarmList);
        rootView =(ViewGroup)inflater.inflate(R.layout.tab_alarm, container, false);

        AlarmList = (ListView)rootView.findViewById(R.id.AlarmList);

        FloatingActionButton btnAdd = (FloatingActionButton)rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Activity alarm call.
                if (v.getId() == R.id.btnAdd)
                    startActivityForResult(new Intent(getContext(), Alarm_setting.class), 1);
            }
        });
        // alarm db에 저장 안해도 돌아갈거 같음.
/*
        database_alarm = new Database_alarm(rootView.getContext());
        Cursor cursor = database_alarm.mySelect();
        while(cursor.moveToNext()) {
            String k = cursor.getString(0);
            al.add(k);
            String i = cursor.getString(1);
            ide.add(i);
            Log.d("데이터베이스","task : "+cursor.getString(cursor.getColumnIndex("task")) + "\nId : "+cursor.getInt(cursor.getColumnIndex("id")));
        }
*/
        //final ArrayAdapter<String> aa = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_checked, al);
        //AlarmList.setAdapter(aa);
        AlarmList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        AlarmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String k = (String)parent.getItemAtPosition(position);


                // sql 디비
                /*
                database = database_alarm.getWritableDatabase();
                database.delete(Database_alarm.TABLE_TASK, ide.get(position) + "=" + Database_alarm.KEY_ID, null);
                */
                Toast.makeText(rootView.getContext(), k + " Removed", Toast.LENGTH_SHORT).show();

                MainActivity ma = (MainActivity)getActivity();
                deleteAlarm(ma.hm.get(k));
                Log.e("알람", "삭제..." + ma.hm.get(k));

                return false;
            }
        });
        return rootView;
    }

    // 자동 알람.
    public void setAlarm(Context context, int alarmId) {
         Log.e("데이터in자동알람", "진입 됨?");
        double sec_time = 0;

        Calendar cal = Calendar.getInstance();
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK); // 1.. 일요일, 2.. 월요일 , ... , 7.. 토요일

        Database_overall db = Database_overall.getInstance(context);

        Cursor c = db.Select();
        c.moveToLast();
        sec_time = c.getDouble(c.getColumnIndex("sec_time"));
        double sec_hour = sec_time / 60, sec_minute = sec_time % 60;

        cal.set(2018,
                5,
                4,
                (c.getInt(c.getColumnIndex("str_hour")) - (int) sec_hour),
                (c.getInt(c.getColumnIndex("str_min")) - (int) sec_minute), 00);
        Log.e("자동알람", "year: " + cal.get(Calendar.YEAR) + ", month: " + cal.get(Calendar.MONTH) + ", day: " + cal.get(Calendar.DAY_OF_MONTH) + ", hour: " + cal.get(Calendar.HOUR_OF_DAY) + ", minute: " + cal.get(Calendar.MINUTE));

/*
        while(c.moveToNext()) {
            if (c.getInt(c.getColumnIndex("_id")) == alarmId) {
                sec_time = c.getDouble(c.getColumnIndex("sec_time"));
            }
        }
        Log.e("데이터베이스in알람5", String.valueOf(sec_time));
        */

        Toast.makeText(context, "Alarm is set @ " + cal.getTime(), Toast.LENGTH_LONG).show();
        Intent callReceiver = new Intent(context, Alarm_receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, callReceiver, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

        alarmList = new String[c.getCount()];
        int count = 0;
        if (c.getCount() == 1) {
            alarmList[count] = c.getString(c.getColumnIndex("event_name"));
        } else {
            boolean isFirst = true;

            while (c.moveToNext()) {
                if (isFirst) {
                    c.moveToFirst();
                    isFirst = false;
                }
                if (c.getInt(c.getColumnIndex("ala_code")) == 1) {
                    alarmList[count] = c.getString(c.getColumnIndex("event_name"));
                    Log.e("되니", "alarmList[" + count + "]: " + alarmList[count]);
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

    // 수동 알람.
    public void setAlarm(Calendar targetcall, int alarmId){
        Toast.makeText(getActivity(),"Alarm is set @ "+targetcall.getTime(),Toast.LENGTH_LONG).show();
        Intent callReceiver = new Intent(getContext(), Alarm_receiver.class);
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
                    Log.e("되니", "alarmList[" + count + "]: " + alarmList[count]);
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
