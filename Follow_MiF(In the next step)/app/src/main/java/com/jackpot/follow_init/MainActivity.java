package com.jackpot.follow_init;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
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
    Tab_schedule F_schedule;        // Schedule Fragment 생성.
    Tab_calendar F_calendar;        // Calendar Fragment 생성.
    Tab_alarm F_alarm;              // Alarm Fragment 생성.
    Tab_setting F_setting;          // Setting Fragment 생성.

    private HashMap<Integer,String> busType;
    private HashMap<Integer,String> subwayCode;

    // SQLite database.
    SQLiteDatabase database;            // Set a SQLite database
    Database_overall DB_helper;      // Call SQLite helper which was over ridden.

    // trafficType 단위로 index 에 하나씩 통으로 저장됨.
    ArrayList<Obj_search> shortest = new ArrayList<Obj_search>();

    // To deal with back button.
    private long pressedTime;

    // Firebase database.
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("Schedule");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a SQLite database.
        DB_helper = new Database_overall(getApplicationContext(), null);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Action bar 역할이 뭐였더라?
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
        F_setting = new Tab_setting();

        // Default page is schedule fragment.(The first page when user open the Follow application.)
        getSupportFragmentManager().beginTransaction().replace(R.id.container, F_schedule).commit();

        TabLayout tabs = (TabLayout)findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Schedule"));
        tabs.addTab(tabs.newTab().setText("Calendar"));
        tabs.addTab(tabs.newTab().setText("Alarm"));
        tabs.addTab(tabs.newTab().setText("Setting"));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                Fragment selected = null;
                if(position == 0)
                    selected = F_schedule;
                else if(position == 1)
                    selected = F_calendar;
                else if(position == 2)
                    selected = F_alarm;
                else if(position == 3)
                    selected = F_setting;

                    getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Firebase DB listener code 부분.
        /*
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // Push 고유키 값 가져오기.
                //String id = databaseReference.child("Schedule").getKey();
                //Log.d("Test DB 0", id);


                Log.d("DBTESTING 1",dataSnapshot.getValue().toString());
                get_data = dataSnapshot.getValue(Obj_schedule.class);
                Log.d("DBTESTING 2", get_data.getDept_name() + "22\n"+get_data.getEnd_hour() + "33\n" + get_data.getEvent_name());

                // 디비에서 read 한 data가 get_data에 저장되는데, 현재는 위도,경도 값 없으니까, 임의로 수동 저장.
                get_data.setDept_latitude(37.4507452);
                get_data.setDept_longitude(127.1288474);
                get_data.setDest_latitude(37.478688);
                get_data.setDept_longitude(127.126174999999);
                Log.d("DBTESTING 333333", String.valueOf(get_data.getDept_latitude()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
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

    //화면회전시 데이터 보존
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

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

    public void HashSubway(){
        subwayCode.put(1,"수도권 1호선");
        subwayCode.put(2,"수도권 2호선");
        subwayCode.put(3,"수도권 3호선");
        subwayCode.put(4,"수도권 4호선");
        subwayCode.put(5,"수도권 5호선");
        subwayCode.put(6,"수도권 6호선");
        subwayCode.put(7,"수도권 7호선");
        subwayCode.put(8,"수도권 8호선");
        subwayCode.put(9,"수도권 9호선");
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
