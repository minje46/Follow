package com.jackpot.follow_init;

import android.content.Intent;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar.add(Calendar.DAY_OF_MONTH, 2);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        ViewGroup rootView =(ViewGroup)inflater.inflate(R.layout.tab_calendar, container, false);

        calendarView = (FlexibleCalendarView) rootView.findViewById(R.id.calendar_view);
        calendarView.setCalendarView(new FlexibleCalendarView.CalendarView() {
            @Override
            public BaseCellView getCellView(int position, View convertView, ViewGroup parent, @BaseCellView.CellType int cellType) {
                BaseCellView cellView = (BaseCellView) convertView;
                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    cellView = (BaseCellView) inflater.inflate(R.layout.calendar_date_cell_view, null);        // 날짜 선택했을 때, 색 변경.
                }
                if (cellType == BaseCellView.OUTSIDE_MONTH) {
                    cellView.setTextColor(getResources().getColor(R.color.date_outside_month_text_color_activity_1));
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
                if (v.getId() == R.id.btnAdd) {
                    startActivityForResult(new Intent(getContext(), Schedule_setting.class), 1);
                }
            }
        });

        Button nextMonthBtn = (Button) rootView.findViewById(R.id.move_to_next_month);
        Button prevMonthBtn = (Button) rootView.findViewById(R.id.move_to_previous_month);
        Button goToCurrentDayBtn = (Button) rootView.findViewById(R.id.go_to_current_day);

        // 달 별 이동 버튼인데, 이건 필요.
        nextMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.moveToNextMonth();
            }
        });
        prevMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.moveToPreviousMonth();
            }
        });

        // 현재 날짜로 이동하는 버튼인데, 이건 옵션.
        goToCurrentDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.goToCurrentDay();
            }
        });

        fillEvents();
        setupToolBar(rootView);

        return rootView;
    }

    // event 있는 거 달력에 표시해주는 method.
    private void fillEvents() {
        calendarView.setEventDataProvider(new FlexibleCalendarView.EventDataProvider() {
            @Override
            public List<CalendarEvent> getEventsForTheDay(int year, int month, int day) {

                if (year == calendar.get(Calendar.YEAR) && month == calendar.get(Calendar.MONTH) && day == calendar.get(Calendar.DAY_OF_MONTH)) {
                    List<CalendarEvent> eventColors = new ArrayList<>(2);
                    eventColors.add(new CalendarEvent(android.R.color.holo_blue_light));
                    eventColors.add(new CalendarEvent(android.R.color.holo_purple));
                    return eventColors;
                }

                if (year == 2016 && month == 10 && day == 12) {
                    List<CalendarEvent> eventColors = new ArrayList<>(2);
                    eventColors.add(new CalendarEvent(android.R.color.holo_blue_light));
                    eventColors.add(new CalendarEvent(android.R.color.holo_purple));
                    return eventColors;
                }

                if (year == 2016 && month == 10 && day == 7 ||
                        year == 2016 && month == 10 && day == 29 ||
                        year == 2016 && month == 10 && day == 5 ||
                        year == 2016 && month == 10 && day == 9) {
                    List<CalendarEvent> eventColors = new ArrayList<>(1);
                    eventColors.add(new CalendarEvent(android.R.color.holo_blue_light));
                    return eventColors;
                }

                if (year == 2016 && month == 10 && day == 31 ||
                        year == 2016 && month == 10 && day == 22 ||
                        year == 2016 && month == 10 && day == 18 ||
                        year == 2016 && month == 10 && day == 11) {
                    List<CalendarEvent> eventColors = new ArrayList<>(3);
                    eventColors.add(new CalendarEvent(android.R.color.holo_red_dark));
                    eventColors.add(new CalendarEvent(android.R.color.holo_orange_light));
                    eventColors.add(new CalendarEvent(android.R.color.holo_purple));
                    return eventColors;
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

        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        bar.setDisplayShowCustomEnabled(true);

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

        bar.setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.action_bar_color_activity_1)));

        //back button color
//        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        upArrow.setColorFilter(getResources().getColor(R.color.title_text_color_activity_1), PorterDuff.Mode.SRC_ATOP);
//        bar.setHomeAsUpIndicator(upArrow);
    }

    // Title 에 보여주는 정보 업데이트. (Parameter 받아서 update)
    private void updateTitle(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        someTextView.setText(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, this.getResources().getConfiguration().locale) + " " + year);
    }

    // 달 바뀌면, Title 또한 바꿔줘야 하니까, 여기서 parameter 던져주기.
    @Override
    public void onMonthChange(int year, int month, int direction) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        updateTitle(year, month);
    }

    // 해당 날짜 눌렀을 때의 event 인데, Toast 가 아닌, 새로운 뷰 띄워서 일정 계획할 수 있도록 하기.
    @Override
    public void onDateClick(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        Toast.makeText(getActivity(), cal.getTime().toString() + " Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            // get data from Schedule setting.
            Bundle get_bundle = data.getExtras();

            if (get_bundle != null) {
                Obj_schedule get_data = (Obj_schedule) get_bundle.getSerializable("object");

                Toast.makeText(getActivity(), "Log data in tab_schedule\nEvent name : "+get_data.getEvent_name()+"\nDept name : "+get_data.getDept_name()+"\nDept_lati : "+get_data.getDept_latitude(),Toast.LENGTH_SHORT).show();

                // Thread 를 생성하고, thread 에서 길찾기 돌리기.
                //Thread waySearch = new Thread_waySearch(getContext(), get_data);
                //waySearch.start();

                //Runnable waySearch = new Runnable_waySearch(getContext(), get_data);
                //waySearch.run();
            }
        }
    }
}

