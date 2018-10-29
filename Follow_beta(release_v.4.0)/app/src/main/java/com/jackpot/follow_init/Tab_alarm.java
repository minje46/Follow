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
import java.util.Collections;
import java.util.Comparator;
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
import static java.lang.Thread.sleep;

/**
 * Created by KWAK on 2018-05-14.
 */

public class Tab_alarm extends Fragment {
    private ViewGroup rootView;
    private ListView AlarmList;
    private MainActivity mainActivity;

    private ArrayList<String> alarmList;      // It stores event_name which has alarm_code 1 from DB and it will be delivered to adapter.
    private ArrayList<Integer> idList = new ArrayList<>();  // idList contains id which is primary key of DB and have same variable "count" for matching with alarmList.
    private ArrayAdapter<String> aa;

    SQLiteDatabase database;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.tab_alarm, container, false);
        AlarmList = (ListView) rootView.findViewById(R.id.AlarmList);
        mainActivity = (MainActivity) getActivity();

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
                Cursor cursor = mainActivity.DB_helper.Select();

                while (cursor.moveToNext()){
                    if(cursor.getInt(cursor.getColumnIndex("_id")) == idList.get(position)){    // 롱클릭한 알람의 아이디가 디비의 아이디랑 매칭 -> 디비 수정을 위해
                        if(cursor.getDouble(cursor.getColumnIndex("dept_lati")) == 0.0){    // 수동 알람 일 때,
                            mainActivity.DB_helper.Delete(String.valueOf(idList.get(position)));
                        } else {        // 자동 알람일 때,
                            ContentValues values = new ContentValues();
                            values.put("ala_code",0);
                            mainActivity.DB_helper.Update(String.valueOf(idList.get(position)), values);
                        }
                    } else {
                        //디비에 매칭되는게 없다?
                    }
                }
                cursor.close();

                deleteAlarm(idList.get(position));
                refresh();

                return false;
            }
        });
        setList();

        return rootView;
    }

    // Manual alarm setting.
    public void setAlarm(Calendar target_call, int alarmId) {
        Intent callReceiver = new Intent(getContext(), Alarm_receiver.class);

        callReceiver.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callReceiver.putExtra("id", alarmId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), alarmId, callReceiver, 0);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, target_call.getTimeInMillis(), pendingIntent);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, target_call.getTimeInMillis(), (long)60*1000, pendingIntent);      //반복 알람 테스팅
        setList();
    }

    // Auto setting alarm for schedule.
    // public void setAlarm(Context context, int alarmId) {
    public void setAlarm(Context context, int alarmId, Cursor cursor) {
        Calendar cal = Calendar.getInstance();
        Log.e("알람 1","진입");
        //Cursor cursor = mainActivity.DB_helper.Select();
        //cursor.moveToLast();
        Log.e("알람 2","진입");
        int sec_min =(int)cursor.getDouble(cursor.getColumnIndex("sec_time"));     // 소요시간 (분)
        int pre_min = cursor.getInt(cursor.getColumnIndex("early_min"));            // 미리 알림시간.(분)
        int tot_min = pre_min + sec_min;
        Log.e("알람3",""+sec_min + pre_min+ String.valueOf(tot_min));
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK); // 1.. 일요일, 2.. 월요일 , ... , 7.. 토요일
        int year = cal.get(Calendar.YEAR); // 2018
        int month = cal.get(Calendar.MONTH); // 0.. 일월, 1.. 이월, ~ , 11..십이월
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cursor.getInt(cursor.getColumnIndex("str_hour"));
        int min = cursor.getInt(cursor.getColumnIndex("str_min"));
        Log.e("알람4",""+day + hour + min);

        int tot_event_start = (hour * 60) + min;        // 일정 시간 분으로 바꾼거(분)

        if((tot_event_start- tot_min) >= 0){
            hour = (tot_event_start - tot_min) / 60;
            min = (tot_event_start - tot_min) % 60;
        } else {                                        // 전날로 알람이 땡겨질떄.
            hour = (1440 + (tot_event_start- tot_min)) / 60;
            min = (1440 + (tot_event_start- tot_min)) % 60;
            day--;
        }

        Log.e("알람4",""+hour + min);

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
                    day = day + 7;
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

        cal.set(year, month, day, hour, min, 00);

        Intent callReceiver = new Intent(context, Alarm_receiver.class);
        callReceiver.putExtra("id", alarmId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, callReceiver, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);      // 일주일 간격 없는 셋팅.
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), (long)24*60*60*1000*7, pendingIntent);  // 일주일 간격.
        cursor.close();

        // Store alarm hour and minute to DB.
        storeDB(String.valueOf(alarmId), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        setList();
    }

    // Auto setting alarm for calendar.
    // public void setAlarm(int alarmId, Context context) {
    public void setAlarm(int alarmId, Context context, Cursor cursor) {
        Calendar cal = Calendar.getInstance();

        //Cursor cursor = mainActivity.DB_helper.Select();
        //cursor.moveToLast();

        int sec_min =(int)cursor.getDouble(cursor.getColumnIndex("sec_time"));     // 소요시간 (분)
        int pre_min = cursor.getInt(cursor.getColumnIndex("early_min"));            // 미리 알림시간.(분)
        int tot_min = pre_min + sec_min;                                                  // 계산을 위한 합. (분)

        Log.e("DB_ALARM", "IN set Alarm1\n" + "Hour : " +Integer.toString(sec_min) + " Min : " +Integer.toString(pre_min));

        int year = cursor.getInt(cursor.getColumnIndex("year"));  // 2018
        int month = cursor.getInt(cursor.getColumnIndex("month")); // 0.. 일월, 1.. 이월, ~ , 11..십이월
        int day = cursor.getInt(cursor.getColumnIndex("day"));
        int hour = cursor.getInt(cursor.getColumnIndex("str_hour"));
        int min = cursor.getInt(cursor.getColumnIndex("str_min"));

        int tot_event_start = (hour * 60) + min;        // 일정 시간 분으로 바꾼거(분)

        if((tot_event_start - tot_min) >= 0){
            hour = (tot_event_start - tot_min) / 60;
            min = (tot_event_start - tot_min) % 60;
        } else {                                        // 전날로 알람이 땡겨질떄.
            hour = (1440 + (tot_event_start- tot_min)) / 60;
            min = (1440 + (tot_event_start- tot_min)) % 60;
            day--;
        }

        cal.set(year, month, day, hour, min, 00);

        cursor.close();

        Intent callReceiver = new Intent(context, Alarm_receiver.class);
        callReceiver.putExtra("id", alarmId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, callReceiver, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

        // Store alarm hour and minute to DB.
        Log.e("DB_Alarm", "Before Store DB method");
        this.storeDB(String.valueOf(alarmId), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        Log.e("DB_Alarm", "Before Set list method");
        this.setList();
        Log.e("DB_Alarm", "After set list method");
    }

    // Set alarm list with storing alarmList which have data from DB and idList which matched with index of alarmList and pk _id.
    public void setList() {
        alarmList = new ArrayList<String>();
        int count = 0;

        // 문제점 : DB 첫 등록시, Null pointer 참조래 시발.
        Cursor cursor = mainActivity.DB_helper.Select();
        while (cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex("ala_code")) == 1) {
                switch (cursor.getInt(cursor.getColumnIndex("weekday"))){  // It is for separating between calendar and schedule from DB.
                    case 1:
                            if(cursor.getInt(cursor.getColumnIndex("ala_hour"))<10)
                                alarmList.add("0"+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_hour"))) +"시 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_min")))+"분" + "     "+"월요일"+  "\n"+cursor.getString(cursor.getColumnIndex("event_name")));
                            else
                                alarmList.add(String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_hour"))) +"시 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_min")))+"분" + "     "+"월요일"+  "\n"+cursor.getString(cursor.getColumnIndex("event_name")));
                            idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                            break;
                        case 2:
                            if(cursor.getInt(cursor.getColumnIndex("ala_hour"))<10)
                                alarmList.add("0"+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_hour"))) +"시 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_min")))+"분" + "     "+"화요일"+  "\n"+cursor.getString(cursor.getColumnIndex("event_name")));
                            else
                                alarmList.add(String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_hour"))) +"시 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_min")))+"분" + "     "+"화요일"+  "\n"+cursor.getString(cursor.getColumnIndex("event_name")));
                            idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                            break;
                        case 3:
                            if(cursor.getInt(cursor.getColumnIndex("ala_hour"))<10)
                                alarmList.add("0"+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_hour"))) +"시 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_min")))+"분" + "     "+"수요일"+  "\n"+cursor.getString(cursor.getColumnIndex("event_name")));
                            else
                                alarmList.add(String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_hour"))) +"시 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_min")))+"분" + "     "+"수요일"+  "\n"+cursor.getString(cursor.getColumnIndex("event_name")));
                            idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                            break;
                        case 4:
                            if(cursor.getInt(cursor.getColumnIndex("ala_hour"))<10)
                                alarmList.add("0"+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_hour"))) +"시 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_min")))+"분" + "     "+"목요일"+  "\n"+cursor.getString(cursor.getColumnIndex("event_name")));
                            else
                                alarmList.add(String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_hour"))) +"시 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_min")))+"분" + "     "+"목요일"+  "\n"+cursor.getString(cursor.getColumnIndex("event_name")));
                            idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                            break;
                        case 5:
                            if(cursor.getInt(cursor.getColumnIndex("ala_hour"))<10)
                                alarmList.add("0"+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_hour"))) +"시 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_min")))+"분" + "     "+"금요일"+  "\n"+cursor.getString(cursor.getColumnIndex("event_name")));
                            else
                                alarmList.add(String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_hour"))) +"시 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_min")))+"분" + "     "+"금요일"+  "\n"+cursor.getString(cursor.getColumnIndex("event_name")));
                            idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                            break;
                        default:
                            /*
                            if(cursor.getInt(cursor.getColumnIndex("ala_hour"))<10)
                                alarmList.add("0"+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_hour"))) +"시 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_min")))+"분" +"     " +"[" +String.valueOf(cursor.getInt(cursor.getColumnIndex("month"))+1) +"월 " +String.valueOf(cursor.getInt(cursor.getColumnIndex("day")))+"일" +"]" +"\n" + cursor.getString(cursor.getColumnIndex("event_name")));
                            else
                                alarmList.add(String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_hour"))) +"시 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_min")))+"분" +"     " +"[" +String.valueOf(cursor.getInt(cursor.getColumnIndex("month"))+1) +"월 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("day")))+"일" +"]" + "\n" + cursor.getString(cursor.getColumnIndex("event_name")));
                            idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                            break;
                            */
                            if(cursor.getInt(cursor.getColumnIndex("ala_hour"))<10)
                                alarmList.add("[" +String.valueOf(cursor.getInt(cursor.getColumnIndex("month"))+1) +"월 " +String.valueOf(cursor.getInt(cursor.getColumnIndex("day")))+"일" +"]" + "     0"+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_hour"))) +"시 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_min")))+"분" +"     " +"\n" + cursor.getString(cursor.getColumnIndex("event_name")));
                            else
                                alarmList.add("[" +String.valueOf(cursor.getInt(cursor.getColumnIndex("month"))+1) +"월 " +String.valueOf(cursor.getInt(cursor.getColumnIndex("day")))+"일" +"]" + "     "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_hour"))) +"시 "+String.valueOf(cursor.getInt(cursor.getColumnIndex("ala_min")))+"분" +"     " +"\n" + cursor.getString(cursor.getColumnIndex("event_name")));                            idList.add(count, cursor.getInt(cursor.getColumnIndex("_id")));
                            break;
                }
                count++;
            }
        }
        cursor.close();

        Ascending ascending = new Ascending();
        Collections.sort(alarmList,ascending);

        if (count != 0) {
            aa = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, alarmList);
            AlarmList.setAdapter(aa);
        }
    }

    // For sorting strings in ascending order.
    class Ascending implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
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

    // Store alarm hour and minutes in DB (Auto alarm setting)
    public void storeDB(String id, int hour, int min){
        Log.e("DB_Alarm", "on Storing DB method");
        ContentValues values = new ContentValues();

        if(hour == 0 && min == 0)
            values.put("ala_code", 0);
         else
            values.put("ala_code", 1);

        values.put("ala_hour", hour);
        values.put("ala_min", min);

        Log.e("DB_ALARM", "IN store DB\n" + "Hour : " +Integer.toString(hour) + " Min : " +Integer.toString(min));

        try{
            // 문제점 : DB 첫 등록시, Null pointer 참조래 시발.
            mainActivity.DB_helper.Update(id,values);

        } catch (Exception e)
        {
            Log.e("DB_Alarm", "디비 접근 못해서 Exception 발생");
            e.printStackTrace();
        }

        Log.e("DB_Alarm", "Finish Store DB method");
    }

    // Store alarm hour and minutes in DB (manual alarm setting)
    public void storeDB(int year, int month, int day, int hour, int min, String event){
        ContentValues values = new ContentValues();
        values.put("event_name", event);
        values.put("year", year);
        values.put("month", month);
        values.put("day", day);
        values.put("ala_hour", hour);
        values.put("ala_min", min);
        values.put("ala_code", 1);

        mainActivity.DB_helper.Insert(values);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // result code == -1 is signal from alarmSetting activity after set some input data in alarmSetting and then call this activity to set alarm.
        if (resultCode == -1) {
            // For refreshing this fragment caz this activity is action after finish typing data from setting page.
            // It should be updated with new data from DB.
           mainActivity.getSupportFragmentManager().beginTransaction().detach(mainActivity.F_alarm).attach(mainActivity.F_alarm).commit();
           Calendar ex = (Calendar) data.getExtras().getSerializable("Calendar");

           storeDB(ex.get(Calendar.YEAR), ex.get(Calendar.MONTH), ex.get(Calendar.DAY_OF_MONTH) ,ex.get(Calendar.HOUR_OF_DAY), ex.get(Calendar.MINUTE), data.getStringExtra("eventName"));
           Cursor cursor = mainActivity.DB_helper.Select();
           cursor.moveToLast();
           setAlarm((Calendar) data.getExtras().getSerializable("Calendar"), cursor.getInt(cursor.getColumnIndex("_id")));
           cursor.close();
        }
    }
}