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

    private ArrayList<Obj_schedule> obj_List = new ArrayList<>();       // data back up 용이래. 이것도 필요없게 만들갔으.

    MainActivity mainActivity;

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
            public void onClick(View v) {   // Send a DB helper object in bundle to access DB in new activity.
                if (v.getId() == R.id.btnAdd) {
                    startActivityForResult(new Intent(getActivity(), Schedule_setting.class), 1);
                }
            }
        });

        // Data back up 용이네, 필요 없어.
        //addList(mainActivity.getStoreObject());

        settingTimeTable();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // DB check 용.
        Cursor cursor = mainActivity.DB_helper.Select();
        while(cursor.moveToNext()){     // DB 내부 Test Log. (지우지 말 것.)
            Log.e("데이터베이스","ID : "+cursor.getInt(cursor.getColumnIndex("_id"))
                    +"\nEvent name : "+cursor.getString(cursor.getColumnIndex("event_name"))
                    +"\nDept name : "+cursor.getString(cursor.getColumnIndex("dept_name"))
                    +"\nDept Lati : "+cursor.getDouble(cursor.getColumnIndex("dept_lati"))
                    +"\nDest name : "+cursor.getString(cursor.getColumnIndex("dest_name"))
                    +"\nDest Long : "+cursor.getDouble(cursor.getColumnIndex("dest_long"))
                    +"\nSect time : "+cursor.getInt(cursor.getColumnIndex("sec_time"))
                    +"\nStart hour : "+cursor.getInt(cursor.getColumnIndex("str_hour"))
                    +"\nEnd minute : "+cursor.getInt(cursor.getColumnIndex("end_min"))
                    +"\nAlarm hour : "+cursor.getInt(cursor.getColumnIndex("ala_hour"))
                    +"\nAlarm minute : "+cursor.getInt(cursor.getColumnIndex("ala_min")));
        }
    }

    private void settingTimeTable(){
        Timetable_view.setTimeTable(Schedule_list);
    }

    // 이게 뭐하는 짓이지..?
    private void addList(ArrayList<Obj_schedule> object) {
        String startTime = new String();
        String endTime = new String();
        for(int i=0; i<object.size(); i++) {
            startTime += String.valueOf(object.get(i).getStart_hour()) + ":" + String.valueOf(object.get(i).getStart_minute());
            endTime += String.valueOf(object.get(i).getEnd_hour()) + ":" + String.valueOf(object.get(i).getEnd_minute());
            Schedule_list.add(new TimeTableModel(i, object.get(i).getStart_hour(), object.get(i).getEnd_hour(), object.get(i).getWeek(), startTime, endTime, object.get(i).getEvent_name()));
            Log.d("Data", "추가됨?"+i);
        }
    }

    private void deleteList(int index){
        Schedule_list.remove(index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == -1){
            Cursor cursor = mainActivity.DB_helper.Select();
            cursor.moveToLast();
            Thread waySearch = new Thread_waySearch(getContext(),cursor.getInt(cursor.getColumnIndex("_id")));
            Toast.makeText(getActivity(),"스케쥴 작성된 PK id : "+cursor.getInt(cursor.getColumnIndex("_id")),Toast.LENGTH_LONG).show();
            waySearch.start();

            /*  스케쥴에 올리기 위해서 data 값 빼오는 부분. -> DB 에서 값 빼오는 걸로 변경 해야 함.
          if (get_bundle != null) {
                Obj_schedule get_data = (Obj_schedule) get_bundle.getSerializable("object");
                obj_List.add(get_data);

                Toast.makeText(getActivity(), "Log data in tab_schedule\nEvent name : "+get_data.getEvent_name()+"\nDept name : "+get_data.getDept_name()+"\nDept_lati : "+get_data.getDept_latitude(),Toast.LENGTH_SHORT).show();

                addList(obj_List);
                mainActivity.storeObject(obj_List);
                settingTimeTable();
             */
        }
    }
}