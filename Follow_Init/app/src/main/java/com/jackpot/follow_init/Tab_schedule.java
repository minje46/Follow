package com.jackpot.follow_init;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by KWAK on 2018-05-14.
 */

public class Tab_schedule extends Fragment implements View.OnClickListener{
    private View mView, btnMode;
    private TimeTableView timeTable;
    private FloatingActionButton fab;

    private String title = new String();

    private List<String> mTitles = Arrays.asList("Korean", "English", "Math", "Science", "Physics", "Chemistry", "Biology");
    private List<String> mLongHeaders = Arrays.asList("/","Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
    private List<String> mShortHeaders = Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");

    private long mNow = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = (ViewGroup) inflater.inflate(R.layout.tab_schedule, container, false);
        fab = mView.findViewById(R.id.plus);
        initLayout();
        initListener();
        initData();
        fab.setOnClickListener(this);
        return mView;
    }

    private void initLayout() {
        timeTable = (TimeTableView) mView.findViewById(R.id.timeTable);
    }

    private void initListener() {
        timeTable.setShowHeader(false);
        timeTable.setTableMode(TimeTableView.TableMode.LONG);
        //timeTable.setTimeTable(getMillis("2017-11-10 00:0000"), mShortSamples);
        timeTable.setTimeTable(mNow, getSamples(mNow, mShortHeaders, mTitles));


        timeTable.setOnTimeItemClickListener(new TimeTableItemViewHolder.OnTimeItemClickListener() {
            @Override
            public void onTimeItemClick(View view, int position, TimeGridData item) {
                TimeData time = item.getTime();
            }
        });
    }

    private void initData() {
        timeTable.setStartHour(7);
        timeTable.setShowHeader(true);
        timeTable.setTableMode(TimeTableView.TableMode.LONG);

        DateTime now = DateTime.now();
        mNow = now.withTimeAtStartOfDay().getMillis();

        //timeTable.setTimeTable(getMillis("2017-11-10 00:00:00"), mLongSamples);
        timeTable.setTimeTable(mNow, getSamples(mNow, mLongHeaders, mTitles));
    }

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

                /*start = end.plusMinutes((int) ((Math.random() * 10) + 1) * 10);
                end = start.plusMinutes((int) ((Math.random() * 10) + 1) * 30);*/
            }

            tables.add(new TimeTableData(headers.get(i), values));
        }
        return tables;
    }


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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.plus:
                Bundle args = new Bundle();
                args.putString("key", "value");

                FragmentDialog dialog = new FragmentDialog();
                dialog.setArguments(args);
                dialog.show(getActivity().getSupportFragmentManager(), "tag");
                break;
        }
    }
}