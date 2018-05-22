package com.jackpot.follow_init;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by KWAK on 2018-05-22.
 */

public class obj_schedule{
    private int year;
    private int month;
    private int day;
    private String event_name;
    private String dept_name;
    private double dept_latitude;
    private double dept_longitude;
    private String dest_name;
    private double dest_latitude;
    private double dest_longitude;
    private int start_hour;
    private int start_minute;
    private int end_hour;
    private int end_minute;
    private double sectionTime;
    private int getAlarm;

    public obj_schedule(){
        year = 0;
        month = 0;
        day = 0;
        event_name = "";
        dept_name = "";
        dept_latitude = 0.0;
        dept_longitude = 0.0;
        dest_name = "";
        dest_latitude = 0.0;
        dest_longitude = 0.0;
        start_hour = 0;
        start_minute = 0;
        end_hour = 0;
        end_minute = 0;
        sectionTime = 0.0;
        getAlarm = 0;
    }

    public obj_schedule(int y, int m, int d, String event, String dp_name, double dp_la, double dp_lg, String ds_name, double ds_la, double ds_lg, int s_h, int s_m, int e_h, int e_m, double st, int code){
        year = y;
        month = m;
        day = d;
        event_name = event;
        dept_name = dp_name;
        dept_latitude = dp_la;
        dept_longitude = dp_lg;
        dest_name = ds_name;
        dest_latitude = ds_la;
        dest_longitude = ds_lg;
        start_hour = s_h;
        start_minute = s_m;
        end_hour = e_h;
        end_minute = e_m;
        sectionTime = st;
        getAlarm = code;
    }

    public int getYear(){
        return year;
    }

    public int getMonth(){
        return month;
    }

    public int getDay(){
        return day;
    }

    public String getEvent_name(){
        return event_name;
    }

    public String getDept_name(){
        return dept_name;
    }

    public double getDept_latitude(){
        return dept_latitude;
    }

    public double getDept_longitude(){
        return dept_longitude;
    }

    public double getDest_latitude(){
        return dest_latitude;
    }

    public double getDest_longitude(){
        return dest_longitude;
    }

    public String getDest_name(){
        return dest_name;
    }

    public int getStart_hour(){
        return start_hour;
    }

    public int getStart_minute(){
        return start_minute;
    }

    public int getEnd_hour(){
        return end_hour;
    }

    public int getEnd_mintue(){
        return end_minute;
    }

    public double getSectionTime() {
        return sectionTime;
    }

    public int getGetAlarm(){
        return getAlarm;
    }

    public void setYear(int y){
        year = y;
    }

    public void setMonth(int m){
        month = m;
    }

    public void setDay(int d){
        day = d;
    }

    public void setEvent_name(String event){
        event_name = event;
    }

    public void setDept_name(String dp_name){
        dept_name = dp_name;
    }

    public void setDest_name(String ds_name){
        dest_name = ds_name;
    }

    public void setDept_latitude(double dept_latitude) {
        this.dept_latitude = dept_latitude;
    }

    public void setDept_longitude(double dept_longitude) {
        this.dept_longitude = dept_longitude;
    }

    public void setDest_latitude(double dest_latitude) {
        this.dest_latitude = dest_latitude;
    }

    public void setDest_longitude(double dest_longitude) {
        this.dest_longitude = dest_longitude;
    }

    public void setEnd_hour(int end_hour) {
        this.end_hour = end_hour;
    }

    public void setEnd_mintue(int end_mintue) {
        this.end_minute = end_mintue;
    }

    public void setGetAlarm(int getAlarm) {
        this.getAlarm = getAlarm;
    }

    public void setStart_hour(int start_hour) {
        this.start_hour = start_hour;
    }

    public void setSectionTime(double sectionTime) {
        this.sectionTime = sectionTime;
    }

    public void setStart_minute(int start_minute) {
        this.start_minute = start_minute;
    }
}
