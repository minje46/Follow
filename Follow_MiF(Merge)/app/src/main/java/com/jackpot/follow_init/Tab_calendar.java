package com.jackpot.follow_init;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.p_v.flexiblecalendar.FlexibleCalendarView;
import com.p_v.flexiblecalendar.entity.CalendarEvent;
import com.p_v.flexiblecalendar.view.BaseCellView;
import com.p_v.flexiblecalendar.view.SquareCellView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by KWAK on 2018-05-14.
 */

public class Tab_calendar extends Fragment implements FlexibleCalendarView.OnMonthChangeListener, FlexibleCalendarView.OnDateClickListener {
    private FlexibleCalendarView calendarView;
    private TextView someTextView, TextEvent;
    private Calendar calendar = Calendar.getInstance();
    private FloatingActionButton btnAdd;

    private MainActivity mainActivity;
    private int temp_year, temp_month, temp_day;
    private Bundle bundle;
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        ViewGroup rootView =(ViewGroup)inflater.inflate(R.layout.tab_calendar, container, false);
        mainActivity = (MainActivity)getActivity();
        bundle = new Bundle();
        TextEvent = rootView.findViewById(R.id.textEvent);

        calendarView = (FlexibleCalendarView) rootView.findViewById(R.id.calendar_view);
        calendarView.setCalendarView(new FlexibleCalendarView.CalendarView() {
            @SuppressLint("ResourceAsColor")
            @Override
            public BaseCellView getCellView(int position, View convertView, ViewGroup parent, @BaseCellView.CellType int cellType) {
                BaseCellView cellView = (BaseCellView) convertView;
                LayoutInflater inflater = LayoutInflater.from(getActivity());

                if (cellType == BaseCellView.SELECTED) {
                    cellView = (BaseCellView) inflater.inflate(R.layout.calendar_date_cell_view, null);
                    cellView.setTextColor(R.color.black_overlay);;

                } else if (cellType == BaseCellView.SELECTED_TODAY) {
                    cellView = (BaseCellView) inflater.inflate(R.layout.calendar_date_cell_view, null);        // 날짜 선택했을 때, 색 변경.
                    cellView.setTextColor(R.color.black_overlay);
                } else if(cellType == BaseCellView.TODAY){
                    cellView = (BaseCellView) inflater.inflate(R.layout.calendar_week_cell_view, null);
                    cellView.setTextColor(R.color.black_overlay);
                }
                return cellView;
            }

            @Override
            public BaseCellView getWeekdayCellView(int position, View convertView, ViewGroup parent) {
                BaseCellView cellView = (BaseCellView) convertView;
                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    //cellView = (SquareCellView) inflater.inflate(R.layout.calendar_week_cell_view, null);  // 일~토 주 표현해주는 공간 색 변경.
                }
                return cellView;
            }

            @Override
            public String getDayOfWeekDisplayValue(int dayOfWeek, String defaultValue) {
                return String.valueOf(defaultValue.charAt(0));
            }
        });
        calendarView.setOnMonthChangeListener(this);
        calendarView.setOnDateClickListener(this);

        btnAdd = rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(),Calendar_setting.class);
                intent.putExtras(bundle);

                if (v.getId() == R.id.btnAdd) {
                    startActivityForResult(intent,1);
                }
            }
        });

        setupToolBar(rootView);         // Set tool bar which show week.
        setUpTextEvent();               // Set space of text view which contains events on that month.
        fillEvents();                   // Set a sign shows whether there is event or not on that day.

        return rootView;
    }

    // Set a sign shows whether there is event or not on that day.
    private void fillEvents() {
        calendarView.setEventDataProvider(new FlexibleCalendarView.EventDataProvider() {
            @Override
            public List<CalendarEvent> getEventsForTheDay(int year, int month, int day) {
                Cursor cursor = mainActivity.DB_helper.Select();
                while(cursor.moveToNext()) {
                    if (year == cursor.getInt(cursor.getColumnIndex("year")) && month == cursor.getInt(cursor.getColumnIndex("month")) && day == cursor.getInt(cursor.getColumnIndex("day"))) {
                        List<CalendarEvent> eventColors = new ArrayList<>(2);
                        Log.d("FILL", "여기 들어오냐?");
                        eventColors.add(new CalendarEvent(android.R.color.holo_blue_light));
                        eventColors.add(new CalendarEvent(android.R.color.holo_purple));
                        return eventColors;
                    }
                }
                cursor.close();
                return null;
            }
        });
    }

    // Set some views related on this fragment in this step. It is called between onCreateView() and onResume(). maybe...?
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateTitle(calendarView.getSelectedDateItem().getYear(), calendarView.getSelectedDateItem().getMonth());
    }

    // Set tool bar which show week probably.
    public void setupToolBar(View mainView) {
        Toolbar toolbar = (Toolbar) mainView.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        bar.setDisplayHomeAsUpEnabled(false);   //이게 화살표
        bar.setDisplayShowTitleEnabled(false);
        bar.setDisplayShowCustomEnabled(true); //이게 연 월 표시


        someTextView = new TextView(getActivity());
        someTextView.setTextColor(getActivity().getResources().getColor(R.color.title_text_color_activity_1));
        someTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarView.isShown()) {
                    calendarView.collapse();
                } else {
                    calendarView.expand();
                }
            }
        });
        bar.setCustomView(someTextView);
        //여기가 캘린더 *월 ****년 적혀있는 부분
        bar.setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
    }

    // Set space of text view which contains events on that month.
    private void setUpTextEvent(){
        String result = "";            // A variable to store all events at once caz of showing contents on textView.
        ArrayList<String> events = new ArrayList<String>();
        String str;
        // Textview 에 띄워줄 string 에 따라 result 변수 쓰냐 안쓰냐 임.
        Cursor cursor = mainActivity.DB_helper.Select();
        while(cursor.moveToNext()) {     // DB check 해서 일정 있는지 확인.
            if (cursor.getInt(cursor.getColumnIndex("year")) == calendar.get(Calendar.YEAR) && cursor.getInt(cursor.getColumnIndex("month")) == calendar.get(Calendar.MONTH)) {
                if(cursor.getInt(cursor.getColumnIndex("day")) < 10) {
                    str = String.valueOf(calendar.get(Calendar.YEAR)) + "년 " + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "월 0" + String.valueOf(cursor.getInt(cursor.getColumnIndex("day"))) + "일 "
                            + "\nEvent name : " + cursor.getString(cursor.getColumnIndex("event_name")) + "\n\n";
                    events.add(str);
                } else {
                    str = String.valueOf(calendar.get(Calendar.YEAR)) + "년 " + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "월 " + String.valueOf(cursor.getInt(cursor.getColumnIndex("day"))) + "일 "
                            + "\nEvent name : " + cursor.getString(cursor.getColumnIndex("event_name")) + "\n\n";
                    events.add(str);
                }
            }
        }
        cursor.close();

        Ascending ascending = new Ascending();
        Collections.sort(events,ascending);

        for(int i = 0; i < events.size(); i++) {
            result += events.get(i).toString() + "\n";
        }

        TextEvent.setText(result);
    }

    // For sorting strings in ascending order.
    class Ascending implements Comparator<String>{
        @Override
        public int compare(String o1, String o2) {
            //return 0;
            return o1.compareTo(o2);
        }
    }

    // Update tool bar which show week probably.
    private void updateTitle(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        someTextView.setText(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, this.getResources().getConfiguration().locale) + " " + year);
    }

    // Update space of text view which contains events on that month.
    private void updateTextEvent(int year, int month){
        String result = "";            // A variable to store all events at once caz of showing contents on textView.
        ArrayList<String> events2 = new ArrayList<String>();
        String str2;
        // Textview 에 띄워줄 string 에 따라 result 변수 쓰냐 안쓰냐 임.

        Cursor cursor = mainActivity.DB_helper.Select();
        while(cursor.moveToNext()) {     // DB check 해서 일정 있는지 확인.
            if (cursor.getInt(cursor.getColumnIndex("year")) == year && cursor.getInt(cursor.getColumnIndex("month")) == month) {
                if (cursor.getInt(cursor.getColumnIndex("day")) < 10) {
                    str2 = String.valueOf(year) + "년 " + String.valueOf(month + 1) + "월 0" + String.valueOf(cursor.getInt(cursor.getColumnIndex("day"))) + "일 "
                            + "\nEvent name : " + cursor.getString(cursor.getColumnIndex("event_name")) + "\n\n";
                    events2.add(str2);
                } else {
                    str2 = String.valueOf(year) + "년 " + String.valueOf(month + 1) + "월 " + String.valueOf(cursor.getInt(cursor.getColumnIndex("day"))) + "일 "
                            + "\nEvent name : " + cursor.getString(cursor.getColumnIndex("event_name")) + "\n\n";
                    events2.add(str2);
                }
            }
        }
        cursor.close();

        Ascending ascending2 = new Ascending();
        Collections.sort(events2,ascending2);

        for(int i = 0; i < events2.size(); i++){
            result += events2.get(i).toString() + "\n";
        }

        TextEvent.setText(result);
    }

    // 달 바뀌면, Title 또한 바꿔줘야 하니까, 여기서 parameter 던져주기.
    @Override
    public void onMonthChange(int year, int month, int direction) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        updateTitle(year, month);
        updateTextEvent(year, month);
        //   ((MainActivity)getContext()).getSupportFragmentManager().beginTransaction().detach(((MainActivity)getContext()).F_calendar).attach(((MainActivity)getContext()).F_calendar).commit();
    }

    // 해당 날짜 눌렀을 때의 event 인데, Toast 가 아닌, 새로운 뷰 띄워서 일정 계획할 수 있도록 하기.
    @Override
    public void onDateClick(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        temp_year = year;
        temp_month = month;
        temp_day = day;
        bundle.putInt("year", temp_year);
        bundle.putInt("month", temp_month);
        bundle.putInt("day", temp_day);


        final AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
        String str = "";

        Cursor cursor = mainActivity.DB_helper.Select();
        while (cursor.moveToNext()) {
            if ((cursor.getInt(cursor.getColumnIndex("year")) == year && cursor.getInt(cursor.getColumnIndex("month")) == month && cursor.getInt(cursor.getColumnIndex("day")) == day)) {
                str += (String)cursor.getString(cursor.getColumnIndex("event_name")) + "\n";
            }
        }
        cursor.close();

        if(str != ""){
            alert.setTitle("이 날의 일정").setMessage(str).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alertDialog = alert.create();
            alertDialog.show();
        }
        else
            Toast.makeText(getContext(),"일정이 없습니다!", Toast.LENGTH_LONG).show();
    }

    // Result code == -1 means user wanna set some data about events from alarm_setting page. It deals with this case.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == -1){
            // For refreshing this fragment caz this activity is action after finish typing data from setting page.
            // It should be updated with new data from DB.
            mainActivity.getSupportFragmentManager().beginTransaction().detach(mainActivity.F_calendar).attach(mainActivity.F_calendar).commit();

            Cursor cursor = mainActivity.DB_helper.Select();
            cursor.moveToLast();
            // 알람 코드 확인하기
            Thread waySearch = new Thread_waySearch(getContext(),cursor.getInt(cursor.getColumnIndex("_id")));
            Toast.makeText(getActivity(),"스케쥴 작성된 PK id : "+cursor.getInt(cursor.getColumnIndex("_id")),Toast.LENGTH_LONG).show();
            waySearch.start();
        }
    }
}