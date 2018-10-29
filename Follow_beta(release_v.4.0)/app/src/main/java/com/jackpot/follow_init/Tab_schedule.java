package com.jackpot.follow_init;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KWAK on 2018-05-14.
 */
public class Tab_schedule extends Fragment {
    private TimeTableView Timetable_view;
    private ArrayList<TimeTableModel> Schedule_list;                 // TimeTableModel = Schedule 에 보여줄 time table 의 형식의 object.
    private FloatingActionButton btnAdd;
    private MainActivity mainActivity;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_schedule, container, false);
        mainActivity = (MainActivity)getActivity();

        Schedule_list = new ArrayList<TimeTableModel>();
        Timetable_view = (TimeTableView) rootView.findViewById(R.id.main_timetable);
        btnAdd = rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btnAdd) {
                    startActivityForResult(new Intent(getActivity(), Schedule_setting.class), 1);
                }
            }
        });
        settingTimeTable();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // DB check 용.
        /*
        Cursor cursor = mainActivity.DB_helper.Select();
        while(cursor.moveToNext()){     // DB 내부 Test Log. (지우지 말 것.)

            Log.e("데이터베이스","ID : "+cursor.getInt(cursor.getColumnIndex("_id"))
                    +"\nEvent name : "+cursor.getString(cursor.getColumnIndex("event_name"))
                    +"\nDept name : "+cursor.getString(cursor.getColumnIndex("dept_name"))
                    +"\nDept Lati : "+cursor.getDouble(cursor.getColumnIndex("dept_lati"))
                    +"\nDest name : "+cursor.getString(cursor.getColumnIndex("dest_name"))
                    +"\nDest Long : "+cursor.getDouble(cursor.getColumnIndex("dest_long"))
                    +"\nYear : "+cursor.getInt(cursor.getColumnIndex("year"))
                    +"\nMonth : "+cursor.getInt(cursor.getColumnIndex("month"))
                    +"\nday : "+cursor.getInt(cursor.getColumnIndex("day"))
                    +"\nweek day : "+cursor.getInt(cursor.getColumnIndex("weekday"))
                    +"\nSect time : "+cursor.getInt(cursor.getColumnIndex("sec_time"))
                    +"\nStart hour : "+cursor.getInt(cursor.getColumnIndex("str_hour"))
                    +"\nEnd minute : "+cursor.getInt(cursor.getColumnIndex("end_min"))
                    +"\nPath type : "+cursor.getInt(cursor.getColumnIndex("path_type"))
                    +"\nEarly min : "+cursor.getInt(cursor.getColumnIndex("early_min"))
                    +"\nAlarm hour : "+cursor.getInt(cursor.getColumnIndex("ala_hour"))
                    +"\nAlarm minute : "+cursor.getInt(cursor.getColumnIndex("ala_min"))
                    +"\nAlarm code : "+cursor.getInt(cursor.getColumnIndex("ala_code")));

            Schedule_list.add(new TimeTableModel(cursor.getInt(cursor.getColumnIndex("_id")),
                    cursor.getInt(cursor.getColumnIndex("str_hour")),
                    cursor.getInt(cursor.getColumnIndex("end_hour")),
                    cursor.getInt(cursor.getColumnIndex("weekday")),
                    cursor.getString(cursor.getColumnIndex("event_name"))));
        }
        cursor.close();
        */
        settingTimeTable();
    }

    private void settingTimeTable(){
        Timetable_view.setTimeTable(Schedule_list);
    }

    // Result code == -1 means user wanna set some data about schedule from schedule_setting page. It deals with this case.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1){
            // For refreshing this fragment caz this activity is action after finish typing data from setting page.
            // It should be updated with new data from DB.
            mainActivity.getSupportFragmentManager().beginTransaction().detach(mainActivity.F_schedule).attach(mainActivity.F_schedule).commit();

            Cursor cursor = mainActivity.DB_helper.Select();
            cursor.moveToLast();
            Thread waySearch = new Thread_waySearch(getContext(),cursor.getInt(cursor.getColumnIndex("_id")));
            waySearch.start();
            cursor.close();
        }
    }
}