package com.jackpot.follow_init;

import java.io.Serializable;

/**
 * Created by KWAK on 2018-05-22.
 */

public class Obj_schedule implements Serializable{
    private String event_name;
    private String dept_name;
    private double dept_latitude;
    private double dept_longitude;
    private String dest_name;
    private double dest_latitude;
    private double dest_longitude;
    private int week;
    private int start_hour;
    private int start_minute;
    private int end_hour;
    private int end_minute;
    private double sectionTime;
    private double tot_distance;
    private double wlk_distance;
    private int getAlarm;



    public Obj_schedule(){
        event_name = "";
        dept_name = "";
        dept_latitude = 0.0;
        dept_longitude = 0.0;
        dest_name = "";
        dest_latitude = 0.0;
        dest_longitude = 0.0;
        week = 0;
        start_hour = 0;
        start_minute = 0;
        end_hour = 0;
        end_minute = 0;
        sectionTime = 0.0;
        tot_distance = 0.0;
        wlk_distance = 0.0;
        getAlarm = 0;
    }

    public Obj_schedule(String event, String dp_name, double dp_la, double dp_lg, String ds_name, double ds_la, double ds_lg, int wk, int s_h, int s_m, int e_h, int e_m, double st, double tdis, double wdis, int code){
        event_name = event;
        dept_name = dp_name;
        dept_latitude = dp_la;
        dept_longitude = dp_lg;
        dest_name = ds_name;
        dest_latitude = ds_la;
        dest_longitude = ds_lg;
        week = wk;
        start_hour = s_h;
        start_minute = s_m;
        end_hour = e_h;
        end_minute = e_m;
        sectionTime = st;
        tot_distance = tdis;
        wlk_distance = wdis;
        getAlarm = code;
    }

    public String getDept_name() {
        return dept_name;
    }

    public String getEvent_name() {
        return event_name;
    }

    public double getDept_latitude() {
        return dept_latitude;
    }

    public double getDept_longitude() {
        return dept_longitude;
    }

    public String getDest_name() {
        return dest_name;
    }

    public double getDest_latitude() {
        return dest_latitude;
    }

    public double getDest_longitude() {
        return dest_longitude;
    }

    public int getWeek() {
        return week;
    }

    public int getStart_hour() {
        return start_hour;
    }

    public int getStart_minute() {
        return start_minute;
    }

    public int getEnd_hour() {
        return end_hour;
    }

    public int getEnd_minute() {
        return end_minute;
    }

    public double getSectionTime() {
        return sectionTime;
    }

    public double getTot_distance() {
        return tot_distance;
    }

    public double getWlk_distance() {
        return wlk_distance;
    }

    public int getGetAlarm() {
        return getAlarm;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public void setDept_latitude(double dept_latitude) {
        this.dept_latitude = dept_latitude;
    }

    public void setDept_longitude(double dept_longitude) {
        this.dept_longitude = dept_longitude;
    }

    public void setDest_name(String dest_name) {
        this.dest_name = dest_name;
    }

    public void setDest_latitude(double dest_latitude) {
        this.dest_latitude = dest_latitude;
    }

    public void setDest_longitude(double dest_longitude) {
        this.dest_longitude = dest_longitude;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public void setStart_hour(int start_hour) {
        this.start_hour = start_hour;
    }

    public void setStart_minute(int start_minute) {
        this.start_minute = start_minute;
    }

    public void setEnd_hour(int end_hour) {
        this.end_hour = end_hour;
    }

    public void setEnd_minute(int end_minute) {
        this.end_minute = end_minute;
    }

    public void setSectionTime(double sectionTime) {
        this.sectionTime = sectionTime;
    }

    public void setTot_distance(double tot_distance) {
        this.tot_distance = tot_distance;
    }

    public void setWlk_distance(double wlk_distance) {
        this.wlk_distance = wlk_distance;
    }

    public void setGetAlarm(int getAlarm) {
        this.getAlarm = getAlarm;
    }
}
