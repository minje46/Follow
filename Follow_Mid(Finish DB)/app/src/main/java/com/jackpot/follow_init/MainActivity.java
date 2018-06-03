package com.jackpot.follow_init;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{

    Toolbar toolbar;
    Tab_schedule F_schedule;        // Schedule Fragment 생성.
    Tab_calendar F_calendar;        // Calendar Fragment 생성.
    Tab_alarm F_alarm;              // Alarm Fragment 생성.
    Tab_setting F_setting;          // Setting Fragment 생성.

    // SQLite database.
    SQLiteDatabase database;            // Set a SQLite database
    Database_overall database_overall;      // Call SQLite helper which was over ridden.

    // Firebase database.
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("Schedule");

    Obj_schedule pass_data = new Obj_schedule();    // send data to Tab_schedule.

    HashMap<String, Integer> hm = new HashMap<String, Integer>();
    ArrayList<Obj_schedule> tempSchdule = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a SQLite database.
        database_overall = new Database_overall(getApplicationContext(), null);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        F_schedule = new Tab_schedule();
        F_calendar = new Tab_calendar();
        F_alarm = new Tab_alarm();
        F_setting = new Tab_setting();

        Intent intent = getIntent();            // get data from Schedule setting in bundle.
        pass_data = (Obj_schedule)intent.getSerializableExtra("object");

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
                Log.d("MainActivity", "Selected tab : " +position);

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

        // DB listener code 부분.
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
    public void storeObject(ArrayList<Obj_schedule> obj){
        tempSchdule = obj;
    }
    public ArrayList<Obj_schedule> getStoreObject(){
        return tempSchdule;
    }
}
