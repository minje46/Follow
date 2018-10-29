package com.jackpot.follow_init;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabs;
    Tab_schedule F_schedule;        // Create Schedule Fragment.
    Tab_calendar F_calendar;        // Create Calendar Fragment.
    Tab_alarm F_alarm;              // Create Alarm Fragment.
    Tab_wayResult F_wayResult;      // Create WayResult Fragment.
    Tutorial F_tutorial;

    SharedPreferences tutorial;     // It is for separating which fragment will show.
    SharedPreferences.Editor sh_edit;

    public HashMap<Integer,String> busType;
    public HashMap<Integer,String> subwayCode;

    // SQLite database.
    SQLiteDatabase database;            // Set a SQLite database
    Database_overall DB_helper;      // Call SQLite helper which was over ridden to store all of data such as schedule, calendar and alarm.
    Database_search DB_result;      // Call SQLite helper which was over ridden to store all results of way searching.

    // Data will be stored as Obj_search unit.
    ArrayList<Obj_search> shortest = new ArrayList<Obj_search>();


    private long pressedTime;       // To deal with back button.
    public int id = -1;
    // Firebase database.
//    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//    private DatabaseReference databaseReference = firebaseDatabase.getReference("Schedule");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a SQLite database.
        DB_helper = new Database_overall(getApplicationContext(), null);
        ContentValues fixed = new ContentValues();
        fixed.put("_id", -1);
        DB_helper.Insert(fixed);
        DB_result = new Database_search(getApplicationContext(), null);
        DB_result.Insert(fixed);

        // It saves whether user uses this app first or not. True means user should do tutorial.
        tutorial = getSharedPreferences("Tutorial",MODE_PRIVATE);
        sh_edit = tutorial.edit();
        sh_edit.putBoolean("First",true);
        sh_edit.commit();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        // Store all data of bus types and way codes of subway.
        busType = new HashMap<>();
        subwayCode = new HashMap<>();

        HashBus();
        HashSubway();

        F_schedule = new Tab_schedule();
        F_calendar = new Tab_calendar();
        F_alarm = new Tab_alarm();
        F_wayResult = new Tab_wayResult();
        F_tutorial = new Tutorial();

        // Default page is schedule fragment.(The first page when user open the Follow application.)
/*
        getSupportFragmentManager().beginTransaction().replace(R.id.container, F_schedule).commit();

        tabs = (TabLayout)findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Schedule"));
        tabs.addTab(tabs.newTab().setText("Calendar"));
        tabs.addTab(tabs.newTab().setText("Alarm"));
        tabs.addTab(tabs.newTab().setText("Setting"));
*/
        getSupportFragmentManager().beginTransaction().replace(R.id.container, F_calendar).commit();

        tabs = (TabLayout)findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Calendar"));
        tabs.addTab(tabs.newTab().setText("Alarm"));
        tabs.addTab(tabs.newTab().setText("Result"));
        tabs.addTab(tabs.newTab().setText("Tutorial"));

    }

    @Override
    protected void onResume() {
        super.onResume();

        Cursor cursor = DB_result.Select();

        if(cursor.getCount() != 0){     // For passing tutorial.
            sh_edit.putBoolean("First", false);
            sh_edit.commit();
        }
        else
            Log.e("DB_Search", "DB_Search is NULL");

        // What is the purpose of getting intent in here??? 아마도 알람 destroy 후에 id 받는거 같은데,
        // 기존 방식 (알람 울리고 나면, 해당 알람 울린 거를 way result 띄워 주었음)

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getIntExtra("id", -1);
/*
            Cursor cursor3 = DB_helper.Select();
            while(cursor3.moveToNext()){
                if(cursor3.getInt(cursor3.getColumnIndex("_id")) == id){
                    ContentValues values = new ContentValues();
                    values.put("ala_code",0);
                    DB_helper.Update(String.valueOf(id), values);
                }
            }*/
        }
        else {
            id = -1;
        }


        Cursor cursor2 = DB_helper.Select();
        if(cursor2.getCount() != 0) {
            while (cursor2.moveToNext()) {
                Log.e("DB_MAIN", "ID : " + cursor2.getInt(cursor2.getColumnIndex("_id"))
                        + "\nEvent name : " +cursor2.getString(cursor2.getColumnIndex("event_name"))
                        + "\nDept name : " +cursor2.getString(cursor2.getColumnIndex("dept_name"))
                        + "\nDest name : " +cursor2.getString(cursor2.getColumnIndex("dest_name"))
                        + "\nSection Time : " + cursor2.getDouble(cursor2.getColumnIndex("sec_time"))
                        + "\nSection Dis : " + cursor2.getDouble(cursor2.getColumnIndex("tot_dis"))
                        + "\nYear : " + cursor2.getInt(cursor2.getColumnIndex("year"))
                        + "\nMonth : " + cursor2.getInt(cursor2.getColumnIndex("month"))
                        + "\nDay : " + cursor2.getInt(cursor2.getColumnIndex("day"))
                        + "\nAlarm H : " + cursor2.getInt(cursor2.getColumnIndex("ala_hour"))
                        + "\nAlarm M : " + cursor2.getInt(cursor2.getColumnIndex("ala_min"))
                        + "\nAlarm code : " + cursor2.getInt(cursor2.getColumnIndex("ala_code")));
            }
        }
        cursor2.close();

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                /*
                Fragment selected = null;
                if(position == 0)
                    selected = F_schedule;
                else if(position == 1)
                    selected = F_calendar;
                else if(position == 2)
                    selected = F_alarm;
                else if(position == 3){
                    if(tutorial.getBoolean("First",true)) {
                        selected = F_tutorial;
                    }
                    else{
                        selected = F_wayResult;
                    }
                }
                */
                Fragment selected = null;
                if(position == 0)
                    selected = F_calendar;
                else if(position == 1)
                    selected = F_alarm;
                else if(position == 2)
                    selected = F_wayResult;
                else if(position == 3){
                        selected = F_tutorial;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    // Dealing with back button on Main activity.
    @Override
    public void onBackPressed() {
        if(pressedTime == 0){
            Toast.makeText(MainActivity.this, "If you click one more time, it will be finished", Toast.LENGTH_SHORT).show();
            pressedTime = System.currentTimeMillis();
        } else{
            int seconds = (int)(System.currentTimeMillis() - pressedTime);

            if(seconds > 2000)
                pressedTime = 0;
            else
                finish();
        }
    }

    // For saving data on horizontal way.
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    // Saving bus type data.
    public void HashBus(){
        busType.put(1, "일반");
        busType.put(2, "좌석");
        busType.put(3, "마을버스");
        busType.put(4, "직행좌석");
        busType.put(5, "공항버스");
        busType.put(6, "간선급행");
        busType.put(10, "외곽");
        busType.put(11, "간선");
        busType.put(12, "지선");
        busType.put(13, "순환");
        busType.put(14, "광역");
        busType.put(15, "급행");
        busType.put(20, "농어촌버스");
        busType.put(21, "제주도 시외형버스");
        busType.put(22, "경기도 시외형버스");
        busType.put(26, "급행간선");
    }

    // Saving subway code data.
    public void HashSubway(){
        subwayCode.put(1,"1호선");
        subwayCode.put(2,"2호선");
        subwayCode.put(3,"3호선");
        subwayCode.put(4,"4호선");
        subwayCode.put(5,"5호선");
        subwayCode.put(6,"6호선");
        subwayCode.put(7,"7호선");
        subwayCode.put(8,"8호선");
        subwayCode.put(9,"9호선");
        subwayCode.put(100,"분당선");
        subwayCode.put(101,"공항철도");
        subwayCode.put(102,"자기부상철도");
        subwayCode.put(104,"경의중앙선");
        subwayCode.put(107,"에버라인");
        subwayCode.put(108,"경춘선");
        subwayCode.put(109,"신분당선");
        subwayCode.put(110,"의정부경전철");
        subwayCode.put(111,"수인선");
        subwayCode.put(112,"경강선");
        subwayCode.put(113,"우이신설선");
        subwayCode.put(21,"인천 1호선");
        subwayCode.put(22,"인천 2호선");
        subwayCode.put(31,"대전 1호선");
        subwayCode.put(41,"대구 1호선");
        subwayCode.put(42,"대구 2호선");
        subwayCode.put(43,"대구 3호선");
        subwayCode.put(51,"광주 1호선");
        subwayCode.put(71,"부산 1호선");
        subwayCode.put(72,"부산 2호선");
        subwayCode.put(73,"부산 3호선");
        subwayCode.put(74,"부산 4호선");
        subwayCode.put(78,"동해선");
        subwayCode.put(79,"부산-김해경전철");
    }
}
