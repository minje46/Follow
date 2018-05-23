package com.jackpot.follow_init;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

public class MainActivity extends AppCompatActivity{

    Toolbar toolbar;
    Tab_schedule F_schedule;        // Schedule Fragment 생성.
    Tab_calendar F_calendar;        // Calendar Fragment 생성.
    Tab_alarm F_alarm;              // Alarm Fragment 생성.
    Tab_setting F_setting;          // Setting Fragment 생성.
    Fragment1 F_map;                // Map Fragment 생성.
    Fragment2 F_search;             // Odsay Fragment 생성.

//    public int turn_Service = 0;

    public int turn_Service = 1;

    private Double dept_latitude = 0.0;
    private Double dept_longitude = 0.0;
    private Double dest_latitude = 0.0;
    private Double dest_longitude = 0.0;


    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("Schedule");
    obj_schedule get_data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        F_schedule = new Tab_schedule();
        F_calendar = new Tab_calendar();
        F_alarm = new Tab_alarm();
        F_setting = new Tab_setting();
        F_map = new Fragment1();
        F_search = new Fragment2();

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
                    //selected = F_alarm;
                    selected = F_search;
                else if(position == 3)
                   // selected = F_setting;
                    selected = F_map;

                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // Push 고유키 값 가져오기.
                //String id = databaseReference.child("Schedule").getKey();
                //Log.d("Test DB 0", id);


                Log.d("Test DB 1",dataSnapshot.getValue().toString());
                get_data = dataSnapshot.getValue(obj_schedule.class);
                Log.d("Test DB 2", get_data.getDept_name() + "\n\n" + get_data.getEvent_name());
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


        startService(new Intent(getApplication(),WayService.class));
    }

    public void showMap(){

        getSupportFragmentManager().beginTransaction().replace(R.id.container, F_map).commit();
//        startActivity(new Intent(getApplication(), map.class));
    }

    public void goSearch(){
        Intent check = new Intent(getApplication(),WayService.class);
        check.putExtra("check", 1);
        check.putExtra("dept_latitude",dept_latitude);
        check.putExtra("dept_longitude",dept_longitude);
        check.putExtra("dest_latitude",dest_latitude);
        check.putExtra("dest_longitude",dest_longitude);
        Log.d("Service test","It came to go search method");
        startService(check);
        Log.d("Service test","It is after call service again");
    }

        public void setDept(Double x, Double y){
            dept_latitude = x;
            dept_longitude = y;
            Log.d("Dept_Coordinate in Main", String.valueOf(dept_latitude) +"\n" +String.valueOf(dept_longitude));


            Fragment send_frag = new Fragment2();
            Bundle send_bund = new Bundle();
            send_bund.putDouble("dept_latitude",dept_latitude);
            send_bund.putDouble("dept_longitude",dept_longitude);
            send_frag.setArguments(send_bund);
          }

         public void setDest(Double x, Double y){
         dest_latitude = x;
         dest_longitude = y;
         Log.d("Dest_Coordinate in Main", String.valueOf(dest_latitude) +"\n" +String.valueOf(dest_longitude));

         Fragment2 send_frag = new Fragment2();
         Bundle send_bund = new Bundle();
         send_bund.putDouble("dest_latitude",dest_latitude);
         send_bund.putDouble("dest_longitude",dest_longitude);
         send_frag.setArguments(send_bund);
         }

         public Double getDept_latitude(){
             return dept_latitude;
         }

         public Double getDept_longitude(){
            return dept_longitude;
         }

         public Double getDest_latitude(){
             return dest_latitude;
         }

         public Double getDest_longitude(){
             return dest_longitude;
         }
}
