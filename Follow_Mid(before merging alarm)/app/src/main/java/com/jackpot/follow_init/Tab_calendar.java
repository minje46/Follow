package com.jackpot.follow_init;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.List;

/**
 * Created by KWAK on 2018-05-14.
 */

public class Tab_calendar extends Fragment implements FlexibleCalendarView.OnMonthChangeListener, FlexibleCalendarView.OnDateClickListener {
    private FlexibleCalendarView calendarView;
    private TextView someTextView;
    private Calendar calendar = Calendar.getInstance();
    private FloatingActionButton btnAdd;

    MainActivity mainActivity;
    private int temp_year, temp_month, temp_day;
    Bundle bundle;
    Intent intent;
    TextView TextEvent;

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
            @Override
            public BaseCellView getCellView(int position, View convertView, ViewGroup parent, @BaseCellView.CellType int cellType) {
                BaseCellView cellView = (BaseCellView) convertView;
                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    cellView = (BaseCellView) inflater.inflate(R.layout.calendar_date_cell_view, null);        // 날짜 선택했을 때, 색 변경.
                }
                /* 전체를 흰색으로 감싸주는 것.
                if (cellType == BaseCellView.OUTSIDE_MONTH) {
                    cellView.setTextColor(getResources().getColor(R.color.white));
                }*/
                if (cellType == BaseCellView.SELECTED) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    cellView = (BaseCellView) inflater.inflate(R.layout.calendar_date_cell_view, null);
                }else if (cellType == BaseCellView.SELECTED_TODAY) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    cellView = (BaseCellView) inflater.inflate(R.layout.calendar_date_cell_view, null);
                }
                return cellView;
            }

            @Override
            public BaseCellView getWeekdayCellView(int position, View convertView, ViewGroup parent) {
                BaseCellView cellView = (BaseCellView) convertView;
                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    cellView = (SquareCellView) inflater.inflate(R.layout.calendar_week_cell_view, null);  // 일~토 주 표현해주는 공간 색 변경.
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
//                    startActivityForResult(new Intent(getContext(), Calendar_setting.class), 1);
                    startActivityForResult(intent,1);
                }
            }
        });

        Button goToCurrentDayBtn = (Button) rootView.findViewById(R.id.go_to_current_day);

        // 현재 날짜로 이동하는 버튼인데, 이건 옵션.
        goToCurrentDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.goToCurrentDay();
            }
        });

        fillEvents();
        setUpTextEvent();
        setupToolBar(rootView);

        return rootView;
    }

    // event 있는 거 달력에 표시해주는 method.
    private void fillEvents() {
        calendarView.setEventDataProvider(new FlexibleCalendarView.EventDataProvider() {
            @Override
            public List<CalendarEvent> getEventsForTheDay(int year, int month, int day) {
                Cursor cursor = mainActivity.DB_helper.Select();
                //cursor.moveToFirst();
                while(cursor.moveToNext()) {
                    if (year == cursor.getInt(cursor.getColumnIndex("year")) && month + 1 == cursor.getInt(cursor.getColumnIndex("month")) && day == cursor.getInt(cursor.getColumnIndex("day"))) {
                        List<CalendarEvent> eventColors = new ArrayList<>(2);
                        eventColors.add(new CalendarEvent(android.R.color.holo_blue_light));
                        eventColors.add(new CalendarEvent(android.R.color.holo_purple));
                        return eventColors;
                    }
                }
                return null;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateTitle(calendarView.getSelectedDateItem().getYear(), calendarView.getSelectedDateItem().getMonth());
    }

    // Tool bar 설정해주는 것인듯.
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

    // Title 에 보여주는 정보 업데이트. (Parameter 받아서 update)
    private void updateTitle(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        someTextView.setText(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, this.getResources().getConfiguration().locale) + " " + year);
    }

    private void updateTextEvent(int year, int month){
        // 달 움직이면 년, 월 바뀐걸 파라미터 던져줘야 함.
        // 그러면 현재 코드에서 현 시간 기준으로 값 저장 하는 것을 코드 바꿔야 함.
        // calendar 객체로 현 날짜 가져오기. DB에 값 찾기 위해서.
        String str = "";

        Cursor cursor = mainActivity.DB_helper.Select();
        while(cursor.moveToNext()) {     // DB check 해서 일정 있는지 확인.
            if (cursor.getInt(cursor.getColumnIndex("year")) == year && cursor.getInt(cursor.getColumnIndex("month"))-1 == month) {
       //         Toast.makeText(getActivity(), "Event name : " + cursor.getString(cursor.getColumnIndex("event_name")), Toast.LENGTH_SHORT).show();

                str += String.valueOf(year)+" "+String.valueOf(cursor.getInt(cursor.getColumnIndex("month")))+" "+String.valueOf(cursor.getInt(cursor.getColumnIndex("day")))+
                        "\nEvent name : " + cursor.getString(cursor.getColumnIndex("event_name"))+"\n\n";
            }
        }
        TextEvent.setText(str);
    }

    private void setUpTextEvent(){
        // 달 움직이면 년, 월 바뀐걸 파라미터 던져줘야 함.
        // 그러면 현재 코드에서 현 시간 기준으로 값 저장 하는 것을 코드 바꿔야 함.
        // calendar 객체로 현 날짜 가져오기. DB에 값 찾기 위해서.
        Calendar cal = Calendar.getInstance();
        Toast.makeText(getActivity(),String.valueOf(cal.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(),String.valueOf(cal.get(Calendar.MONTH)),Toast.LENGTH_SHORT).show();
        String str = "";
        int current_year = cal.get(Calendar.YEAR);
        int current_month = cal.get(Calendar.MONTH) + 1;

        Cursor cursor = mainActivity.DB_helper.Select();
        while(cursor.moveToNext()) {     // DB check 해서 일정 있는지 확인.
            if (cursor.getInt(cursor.getColumnIndex("year")) == current_year && cursor.getInt(cursor.getColumnIndex("month")) == current_month) {
            //    Toast.makeText(getActivity(), "Event name : " + cursor.getString(cursor.getColumnIndex("event_name")), Toast.LENGTH_SHORT).show();

                str += String.valueOf(current_year)+" "+String.valueOf(current_month)+" "+String.valueOf(cursor.getInt(cursor.getColumnIndex("day")))+
                        "\nEvent name : " + cursor.getString(cursor.getColumnIndex("event_name"))+"\n\n";
            }
        }
        TextEvent.setText(str);
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
        Toast.makeText(getActivity(), cal.getTime().toString() + " Clicked", Toast.LENGTH_SHORT).show();

        temp_year = year;
        temp_month = month + 1;
        temp_day = day;
        bundle.putInt("year",temp_year);
        bundle.putInt("month",temp_month);
        bundle.putInt("day",temp_day);

        Cursor cursor = mainActivity.DB_helper.Select();
        while(cursor.moveToNext()) {     // DB check 해서 일정 있는지 확인.
            if(cursor.getInt(cursor.getColumnIndex("year")) == year && cursor.getInt(cursor.getColumnIndex("month")) == month && cursor.getInt(cursor.getColumnIndex("day")) == day ){
                Toast.makeText(getActivity(),"Event name : "+cursor.getString(cursor.getColumnIndex("event_name")),Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getActivity(),"일정 맞는게 없쪄염",Toast.LENGTH_SHORT).show();
        }
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
        }
    }
}