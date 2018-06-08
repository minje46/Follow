package com.jackpot.follow_init;

import java.util.ArrayList;

/**
 * Created by KWAK on 2018-06-05.
 */

public class Obj_search {
    private int pathType;                       // 1 - 지하철, 2 - 버스, 3 - 버스+지하철.
    private int trafficType;                    // 1 - 지하철, 2 - 버스, 3 - 도보.
    private int section_time;
    private int section_distance;
    private int wayCode;         // 지하철 호선 표시.
    private int busNo;           // 버스 번호 표시.
    private int type;            // 버스 종류 표시.
    private String startName;        // 승차 장소.
    private String endName;          // 하자 장소.

    public Obj_search(){
        pathType = 0;
        trafficType = 0;
        section_time = 0;
        section_distance = 0;
        wayCode = 0;
        busNo = 0;
        type = 0;
        startName = "";
        endName = "";
    }

    public Obj_search(int pathType, int trafficType, int section_time, int section_distance, int wayCode, int busNo, int type, String startName, String endName) {
        this.pathType = pathType;
        this.trafficType = trafficType;
        this.section_time = section_time;
        this.section_distance = section_distance;
        this.wayCode = wayCode;
        this.busNo = busNo;
        this.type = type;
        this.startName = startName;
        this.endName = endName;
    }

    public int getPathType() {
        return pathType;
    }

    public int getTrafficType() {
        return trafficType;
    }

    public int getSection_time() {
        return section_time;
    }

    public int getSection_distance() {
        return section_distance;
    }

    public int getWayCode() {
        return wayCode;
    }

    public int getBusNo() {
        return busNo;
    }

    public int getType() {
        return type;
    }

    public String getStartName() {
        return startName;
    }

    public String getEndName() {
        return endName;
    }

    public void setPathType(int pathType) {
        this.pathType = pathType;
    }

    public void setTrafficType(int trafficType) {
        this.trafficType = trafficType;
    }

    public void setSection_time(int section_time) {
        this.section_time = section_time;
    }

    public void setSection_distance(int section_distance) {
        this.section_distance = section_distance;
    }

    public void setWayCode(int wayCode) {
        this.wayCode = wayCode;
    }

    public void setBusNo(int busNo) {
        this.busNo = busNo;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }
}
