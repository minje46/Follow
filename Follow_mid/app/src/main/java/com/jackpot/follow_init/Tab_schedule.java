package com.jackpot.follow_init;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.eunsiljo.timetablelib.data.TimeData;
import com.github.eunsiljo.timetablelib.data.TimeGridData;
import com.github.eunsiljo.timetablelib.data.TimeTableData;
import com.github.eunsiljo.timetablelib.view.TimeTableView;
import com.github.eunsiljo.timetablelib.viewholder.TimeTableItemViewHolder;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by KWAK on 2018-05-14.
 */
public class Tab_schedule extends Fragment{
   // private View rootView;
    private FloatingActionButton btnAdd;

    private TimeTableView timeTable;
    private List<String> mTitles = Arrays.asList("Korean", "English", "Math");
    private List<String> mShortHeaders = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri");

    private long mNow = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_schedule, container, false);

        btnAdd = rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.btnAdd)
                    startActivity(new Intent(getContext(), Schedule_setting.class));
            }
        });

        timeTable = (TimeTableView) rootView.findViewById(R.id.timeTable);
        timeTable.setShowHeader(true);
        timeTable.setStartHour(7);
        timeTable.setTableMode(TimeTableView.TableMode.SHORT);
        timeTable.setOnTimeItemClickListener(new TimeTableItemViewHolder.OnTimeItemClickListener() {
            @Override
            public void onTimeItemClick(View view, int position, TimeGridData item) {
                TimeData time = item.getTime();
                Toast.makeText(getActivity(), time.getTitle() + ", " + new DateTime(time.getStartMills()).toString() +
                                " ~ " + new DateTime(time.getStopMills()).toString(), Toast.LENGTH_LONG).show();
            }
        });

        initData();

        return rootView;
    }

    private void initData() {
        DateTime now = DateTime.now();
        mNow = now.withTimeAtStartOfDay().getMillis();

        //timeTable.setTimeTable(getMillis("2017-11-10 00:0000"), mShortSamples);
        //timeTable.setTimeTable(mNow, getSamples(mNow, mShortHeaders, mTitles));
        timeTable.setTimeTable(mNow,setSchedule(mNow, mShortHeaders));
    }

    private ArrayList<TimeTableData>setSchedule(long time, List<String>day){
        // 색깔 관련인거 같고,
        TypedArray colors_table = getResources().obtainTypedArray(R.array.colors_table);
        TypedArray colors_table_light = getResources().obtainTypedArray(R.array.colors_table_light);
  //      int color = colors_table_light.getResourceId(1, 0);
        int color = R.color.black;
        int textColor = R.color.white;

        // 데이터 저장하는 객체들 리스트로 설정.
        ArrayList<TimeTableData> tables = new ArrayList<>();
        ArrayList<TimeData> values = new ArrayList<>();


        TimeData timeData = new TimeData(1, day.get(0), color, textColor, 2108-05-26-8-57-00, 2108-05-26-13-20-00);
        values.add(timeData);
        tables.add(new TimeTableData("Koreanaaaaaaa", values));
        return tables;
    }

    /*
    private ArrayList<TimeTableData> getSamples(long date, List<String> headers, List<String> titles) {
        TypedArray colors_table = getResources().obtainTypedArray(R.array.colors_table);
        TypedArray colors_table_light = getResources().obtainTypedArray(R.array.colors_table_light);

        ArrayList<TimeTableData> tables = new ArrayList<>();
        for (int i = 0; i < headers.size(); i++) {
            ArrayList<TimeData> values = new ArrayList<>();
            DateTime start = new DateTime(date);
            DateTime end = start.plusMinutes((int) ((Math.random() * 10) + 1) * 30);
            for (int j = 0; j < titles.size(); j++) {
                int color = colors_table_light.getResourceId(j, 0);
                int textColor = R.color.black;
                //TEST
                if (headers.size() == 2 && i == 1) {
                    color = colors_table.getResourceId(j, 0);
                    textColor = R.color.black;
                }

                TimeData timeData = new TimeData(j, titles.get(j), color, textColor, start.getMillis(), end.getMillis());

                //TEST
                if (headers.size() == 2 && j == 2) {
                    timeData.setShowError(true);
                }
                values.add(timeData);

                //start = end.plusMinutes((int) ((Math.random() * 10) + 1) * 10);
                //end = start.plusMinutes((int) ((Math.random() * 10) + 1) * 30);
            }

            tables.add(new TimeTableData(headers.get(i), values));
        }
        return tables;
    }
    */


    // =============================================================================
    // Date format
    // =============================================================================
    private long getMillis(String day) {
        DateTime date = getDateTimePattern().parseDateTime(day);
        return date.getMillis();
    }

    private DateTimeFormatter getDateTimePattern() {
        return DateTimeFormat.forPattern("yyyy-MM-dd");
    }
}

