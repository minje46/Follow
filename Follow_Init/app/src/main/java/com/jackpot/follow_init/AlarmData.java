package com.jackpot.follow_init;

import android.database.Cursor;

import com.google.android.gms.internal.firebase_database.zzcc;
import com.google.android.gms.internal.firebase_database.zzdn;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class AlarmData {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int alarmId;
    private String event_name;


    public AlarmData() {
        this.year = 0;
        this.month = 0;
        this.day = 0;
        this.hour = 0;
        this.minute = 0;
        this.event_name = "";
        this.alarmId = -1;
    }

    public AlarmData(String event_name) {
        super();
        this.event_name = event_name;
    }

    public AlarmData(String event_name, int alarmId) {
        super();
        this.event_name = event_name;
        this.alarmId = alarmId;
    }

    public AlarmData(int year, int month, int day, int hour, int minute, int alarmId, String event_name) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.event_name = event_name;
        this.alarmId = alarmId;
    }

    public int nameToId(String event_name) {
        if (this.event_name.equals(event_name)) {
            return this.alarmId;
        }
        return -1;
    }

    // getter methods
    public int getYear() {
        return year;
    }
    public int getMonth() {
        return month;
    }
    public int getDay() {
        return day;
    }
    public int getHour() {
        return hour;
    }
    public int getMinute() {
        return minute;
    }
    public String getEvent_name() {
        return event_name;
    }
    public int getAlarmId() {
        return alarmId;
    }

    // setter methods
    public void setYear(int year) {
        this.year = year;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public void setDay(int day) {
        this.day = day;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }
    public void setMinute(int minute) {
        this.minute = minute;
    }
    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }
    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }
}
