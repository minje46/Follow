package com.jackpot.follow_init;

/**
 * Created by KWAK on 2018-05-27.
 */

public class TimeTableModel {
    private int id;
    private int startnum;
    private int endnum;
    private int week;
    private String starttime="";
    private String endtime="";
    private String name="";

    @Override
    public String toString() {
        return "TimeTableModel [id=" + id + ", startnum=" + startnum + ", endnum=" + endnum + ", week=" + week + ", starttime=" + starttime + ", endtime=" + endtime + ", name=" + name + "]";
    }

    public int getId() {
        return id;
    }

    public int getStartnum() {
        return startnum;
    }

    public int getEndnum() {
        return endnum;
    }

    public int getWeek() {
        return week;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStartnum(int startnum) {
        this.startnum = startnum;
    }

    public void setEndnum(int endnum) {
        this.endnum = endnum;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TimeTableModel() {
        // TODO Auto-generated constructor stub
    }

    public TimeTableModel(int id, int startnum, int endnum, int week, String starttime, String endtime, String name) {
        super();
        this.id = id;
        this.startnum = startnum;
        this.endnum = endnum;
        this.week = week;
        this.starttime = starttime;
        this.endtime = endtime;
        this.name = name;
    }
}