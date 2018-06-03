package com.jackpot.follow_init;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KWAK on 2018-05-14.
 */
public class Tab_schedule extends Fragment {

    private TimeTableView mTimeTableView;
    private ArrayList<TimeTableModel> mList;
    private FloatingActionButton btnAdd;

    private ArrayList<Obj_schedule> obj_List = new ArrayList<>();

    private int id = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_schedule, container, false);

        mList = new ArrayList<TimeTableModel>();
        mTimeTableView = (TimeTableView) rootView.findViewById(R.id.main_timetable_ly);
        btnAdd = rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btnAdd) {
                    startActivityForResult(new Intent(getContext(), Schedule_setting.class), 1);
                }
            }
        });

        MainActivity mainActivity = (MainActivity)getActivity();
        addList(mainActivity.getStoreObject());

        //mTimeTableView.setTimeTable(mList);
        settingTimeTable();
        return rootView;

    }

    private void settingTimeTable(){
        mTimeTableView.setTimeTable(mList);
    }

    private void addList(ArrayList<Obj_schedule> object) {
        String startTime = new String();
        String endTime = new String();
        for(int i=0; i<object.size(); i++) {
            startTime += String.valueOf(object.get(i).getStart_hour()) + ":" + String.valueOf(object.get(i).getStart_minute());
            endTime += String.valueOf(object.get(i).getEnd_hour()) + ":" + String.valueOf(object.get(i).getEnd_minute());
            mList.add(new TimeTableModel(i, object.get(i).getStart_hour(), object.get(i).getEnd_hour(), object.get(i).getWeek(), startTime, endTime, object.get(i).getEvent_name()));
            Log.d("Data", "추가됨?"+i);
        }
    }

    private void deleteList(int index){
        mList.remove(index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            // get data from Schedule setting.
            Bundle get_bundle = data.getExtras();
            MainActivity mainActivity = (MainActivity)getActivity();

            if (get_bundle != null) {
                Obj_schedule get_data = (Obj_schedule) get_bundle.getSerializable("object");
                obj_List.add(get_data);

                Toast.makeText(getActivity(), "Log data in tab_schedule\nEvent name : "+get_data.getEvent_name()+"\nDept name : "+get_data.getDept_name()+"\nDept_lati : "+get_data.getDept_latitude(),Toast.LENGTH_SHORT).show();

                addList(obj_List);
                mainActivity.storeObject(obj_List);
                settingTimeTable();


                // SQLite에 값 저장.
                ContentValues schedule_setting = new ContentValues();

                schedule_setting.put("event_name",get_data.getEvent_name());
                schedule_setting.put("dept_name",get_data.getDept_name());
                schedule_setting.put("dest_name",get_data.getDest_name());

                schedule_setting.put("week", get_data.getWeek());
                schedule_setting.put("str_hour",get_data.getStart_hour());
                schedule_setting.put("str_min",get_data.getStart_minute());
                schedule_setting.put("end_hour",get_data.getEnd_hour());
                schedule_setting.put("end_min",get_data.getEnd_minute());

                schedule_setting.put("dept_lati",get_data.getDept_latitude());
                schedule_setting.put("dept_long",get_data.getDept_longitude());
                schedule_setting.put("dest_lati",get_data.getDest_latitude());
                schedule_setting.put("dest_long",get_data.getDest_longitude());

                MainActivity ma = (MainActivity)getActivity();
                ma.database_overall.Insert(schedule_setting);

                Cursor cursor = ma.database_overall.Select();
                cursor.moveToLast();
                Thread waySearch = new Thread_waySearch(getContext(),ma.database_overall,cursor.getInt(cursor.getColumnIndex("_id")));
                Toast.makeText(getActivity(),"스케쥴 작성된 PK id : "+cursor.getInt(cursor.getColumnIndex("_id")),Toast.LENGTH_LONG).show();
                waySearch.start();
/*
                try{
                    waySearch.join();
                } catch (Exception e){
                    e.printStackTrace();
                }
                waySearch.getResult();
                */
            }
        }
    }
}