package com.jackpot.follow_init;

import android.content.Intent;
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

        mTimeTableView.setTimeTable(mList);

        return rootView;
    }


    private void addList(ArrayList<Obj_schedule> object) {
        String startTime = new String();
        String endTime = new String();
        for(int i=0; i<object.size(); i++) {
            startTime += String.valueOf(object.get(i).getStart_hour()) + String.valueOf(object.get(i).getStart_minute());
            endTime += String.valueOf(object.get(i).getEnd_hour()) + String.valueOf(object.get(i).getEnd_minute());
            mList.add(new TimeTableModel(i, object.get(i).getStart_hour(), object.get(i).getEnd_hour(), 1, startTime, endTime, object.get(i).getEvent_name()));
            Log.d("Data", "추가됨?");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            // get data from Schedule setting.
            Bundle get_bundle = data.getExtras();

            if (get_bundle != null) {
                Obj_schedule get_data = (Obj_schedule) get_bundle.getSerializable("object");
                obj_List.add(get_data);

                Toast.makeText(getActivity(), "Log data in tab_schedule\nEvent name : "+get_data.getEvent_name()+"\nDept name : "+get_data.getDept_name()+"\nDept_lati : "+get_data.getDept_latitude(),Toast.LENGTH_SHORT).show();

                addList(obj_List);
                mTimeTableView.setTimeTable(mList);

                // Thread 를 생성하고, thread 에서 길찾기 돌리기.

                Thread waySearch = new Thread_waySearch(getContext(), get_data);
                waySearch.start();


/*
                try{
                    waySearch.join();
                } catch (Exception e){
                    e.printStackTrace();
                }
                waySearch.getResult();
                */


               // Runnable waySearch = new Runnable_waySearch(getContext(), get_data);
               // waySearch.run();
            }
        }
    }
}