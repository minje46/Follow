package com.jackpot.follow_init;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

    private TimeTableView mTimaTableView;
    private List<TimeTableModel> mList;
    private FloatingActionButton btnAdd;
    private Obj_schedule fromMain = new Obj_schedule();
    private int id = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_schedule, container, false);

        mList = new ArrayList<TimeTableModel>();
        mTimaTableView = (TimeTableView) rootView.findViewById(R.id.main_timetable_ly);

        btnAdd = rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btnAdd) {
                    startActivityForResult(new Intent(getContext(), Schedule_setting.class), 1);
                }
            }
        });

        mTimaTableView.setTimeTable(mList);

        return rootView;
    }


    private void addList(Obj_schedule object) {
        mList.add(new TimeTableModel(id, object.getStart_hour(), object.getEnd_hour(), 1, "8:20", "10:10", object.getEvent_name(),
                "이주형", "IT 대학", "2-13"));
        id++;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Data", "data3");
        Bundle extra = getArguments();
        if(extra!=null) {
            Log.d("Data", "data4");
            fromMain = (Obj_schedule) extra.getSerializable("object");
            addList(fromMain);
//            mTimaTableView.setTimeTable(mList);
            Log.d("Data", "data5");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(getActivity(), "22Event name : ",Toast.LENGTH_SHORT).show();

        if (data != null) {
            // get data from Schedule setting.

            Bundle get_bundle = data.getExtras();

            if (get_bundle != null) {
                Obj_schedule get_data = (Obj_schedule) get_bundle.getSerializable("object");

                Toast.makeText(getActivity(), "33Event name : "+get_data.getEvent_name()+"\nDept name : "+get_data.getDept_name()+"\nDept_lati : "+get_data.getDept_latitude(),Toast.LENGTH_SHORT).show();
            }
        }
    }
}