package com.jackpot.follow_init;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by KWAK on 2018-05-30.
 */

// Thread 지우기. handler 객체 전달을 위해 runnable 을 이용
public class Thread_waySearch extends Thread{

    private ODsayService odsayService;
    private JSONObject jsonObject;

    private Context mContext;
    private int primaryKey;

    private double time = 0;
    private double t_dis = 0;
    private double w_dis = 0;

    SQLiteDatabase database;
    Database_overall DB_helper;

    SQLiteDatabase db;
    Database_search DB_result;

    Obj_search buf;

    public Thread_waySearch(Context context) {
        this.mContext = context;
    }

    public Thread_waySearch(Context context, int id){
        this.mContext = context;
        primaryKey = id;
    }

    @Override
    public void run() {
        super.run();

        odsayService = ODsayService.init(mContext, mContext.getString(R.string.odsay_API_Key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        // Using singleton dp, it makes access database directly on Thread as well.
        DB_helper = Database_overall.getInstance(mContext);
        Cursor cursor = DB_helper.Select();

        // 제대로 되는 거 같긴 한데, 알람이 안울린 경우는 길찾기에서 sec_time 이 계산 안되었을 때인가?
        while(cursor.moveToNext()){
            Log.e("데이터베이스in길찾기","ID : "+cursor.getInt(cursor.getColumnIndex("_id")));
            if(cursor.getInt(cursor.getColumnIndex("_id")) == primaryKey)
                    odsayService.requestSearchPubTransPath(String.valueOf(cursor.getDouble(cursor.getColumnIndex("dept_long"))), String.valueOf(cursor.getDouble(cursor.getColumnIndex("dept_lati"))), String.valueOf(cursor.getDouble(cursor.getColumnIndex("dest_long"))), String.valueOf(cursor.getDouble(cursor.getColumnIndex("dest_lati"))), "0", "0", "0", onResultCallbackListener);
        }

        // Thread 종료하는 것. (run 실행 완료하면 자동으로 종료되긴 한다.)
        //this.interrupt();
    }

    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            jsonObject = oDsayData.getJson();       // 전체 대중교통 길찾기 data 를 Json 으로 불러오는 것.

            try {
                // 길찾기 모든 경로 서칭. (pathType array 하나가 경로 단위)
                JSONArray all_path = (JSONArray)jsonObject.getJSONObject("result").get("path");
                Log.d("진쓰레드 All path", all_path.toString());

                // subPath 의 child 중 traffic 을 저장.
                ArrayList<JSONObject> traffic = new ArrayList<JSONObject>();

                //for(int i = 0; i < all_path.length(); i++) {
                for(int i = 0; i < 1; i++) {        //최소시간 하나만 받을거니까.
                    // 하나의 경로의 세부 정보. (subPath array 가 pathType 의 세부 정보.)
                    JSONArray sub_path = (JSONArray)all_path.getJSONObject(i).get("subPath");
                    Log.d("진쓰레드 subPath", sub_path.toString());

                    for(int j = 0; j < sub_path.length(); j++){
                        // sub path 의 traffic 단위 저장.
                        traffic.add(j, sub_path.getJSONObject(j));
                        time += traffic.get(j).getInt("sectionTime");
                        t_dis += traffic.get(j).getInt("distance");

                        buf = new Obj_search();
                        buf.setTrafficType(traffic.get(j).getInt("trafficType"));

                        Log.e("진쓰레드 traffic", traffic.get(j).toString());
                        Log.e("진쓰레드 total time",String.valueOf(time));

                        switch (traffic.get(j).getInt("trafficType")){
                            case 1:     // 1 is subway
                                buf.setSection_time(traffic.get(j).getInt("sectionTime"));
                                buf.setSection_distance(traffic.get(j).getInt("distance"));
                                buf.setStartName(traffic.get(j).getString("startName"));
                                buf.setEndName(traffic.get(j).getString("endName"));
                                buf.setWayCode(traffic.get(j).getJSONArray("lane").getJSONObject(0).getInt("subwayCode"));
                                break;

                            case 2:     // 2 is bus
                                buf.setSection_time(traffic.get(j).getInt("sectionTime"));
                                buf.setSection_distance(traffic.get(j).getInt("distance"));
                                buf.setStartName(traffic.get(j).getString("startName"));
                                buf.setEndName(traffic.get(j).getString("endName"));
                                buf.setType(traffic.get(j).getJSONArray("lane").getJSONObject(0).getInt("type"));
                                buf.setBusNo(traffic.get(j).getJSONArray("lane").getJSONObject(0).getInt("busNo"));
                                break;

                            case 3:     // 3 is walking.
                                w_dis += traffic.get(j).getInt("distance");
                                buf.setSection_time(traffic.get(j).getInt("sectionTime"));
                                buf.setSection_distance(traffic.get(j).getInt("distance"));
                                break;
                        }
                        (((MainActivity)mContext).shortest).add(j,buf);
                    }
                }
                // 메인 객체에 길찾기 결과값 저장 제대로 된거 체크.
                for(int i = 0; i < (((MainActivity)mContext).shortest).size(); i++)
                {
                    Log.e("진쓰레드값 메인에 traffic",String.valueOf((((MainActivity)mContext).shortest).get(i).getTrafficType()));
                    Log.e("진쓰레드값 메인에 번호",String.valueOf((((MainActivity)mContext).shortest).get(i).getBusNo()));
                    Log.e("진쓰레드값 메인에 하차",(((MainActivity)mContext).shortest).get(i).getEndName());
                }
                // 길찾기 결과 값을 result 디비에 저장.
                Store_result();

                // 길찾기 결과 값 Main db에 값저장.
                database = DB_helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("sec_time", time);
                values.put("tot_dis",t_dis);
                values.put("wak_dis",w_dis);
                //values.put("ala_code", 1);
                database.update("dataTable", values, "_id=?", new String[]{String.valueOf(primaryKey)});

                Cursor cursor = DB_helper.Select();
                cursor.moveToLast();

                // 길찾기 완료 후, Tab_alarm 호출. (For setting alarm automatically)
                if (cursor.getInt(cursor.getColumnIndex("year")) == 0) { // 스케쥴 알람 세팅
                    ((MainActivity)mContext).F_alarm.setAlarm(mContext, primaryKey);
                }
                else { // 캘린더 알람 세팅
                    ((MainActivity)mContext).F_alarm.setAlarm(primaryKey, mContext);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(int i, String errorMessage, API api) {
            Log.d("Service Test", "Result call back fail");
        }
    };

    public void Store_result(){
        // 길찾기 결과 값 result db에 값저장.
        for(int i = 0; i < (((MainActivity)mContext).shortest).size(); i++)
        {
            ContentValues values = new ContentValues();

            values.put("fk", primaryKey);
            values.put("path_type",(((MainActivity)mContext).shortest).get(i).getPathType());
            values.put("traffic_type",(((MainActivity)mContext).shortest).get(i).getTrafficType());
            values.put("section_time",(((MainActivity)mContext).shortest).get(i).getSection_time());
            values.put("section_dis",(((MainActivity)mContext).shortest).get(i).getSection_distance());
            switch ((((MainActivity)mContext).shortest).get(i).getTrafficType()){
                case 1:     // 1 is subway
                    values.put("start_name",(((MainActivity)mContext).shortest).get(i).getStartName());
                    values.put("end_name",(((MainActivity)mContext).shortest).get(i).getEndName());
                    values.put("way_code",(((MainActivity)mContext).shortest).get(i).getWayCode());
                    break;

                case 2:     // 2 is bus
                    values.put("start_name",(((MainActivity)mContext).shortest).get(i).getStartName());
                    values.put("end_name",(((MainActivity)mContext).shortest).get(i).getEndName());
                    values.put("bus_no",(((MainActivity)mContext).shortest).get(i).getBusNo());
                    values.put("type",(((MainActivity)mContext).shortest).get(i).getType());
                    break;
            }
            ((MainActivity)mContext).DB_result.Insert(values);
        }

        // 쓰레드에서 디비2에 제대로 들어갔는지 테스트

        Cursor cursor = ((MainActivity)mContext).DB_result.Select();
        while(cursor.moveToNext()){
            Log.e("디비2","ID : "+cursor.getInt(cursor.getColumnIndex("_id"))
                    +"\nFK : "+cursor.getInt(cursor.getColumnIndex("fk"))
                    +"\nPath Type : "+cursor.getInt(cursor.getColumnIndex("path_type"))
                    +"\nTraffic Type : "+cursor.getInt(cursor.getColumnIndex("traffic_type"))
                    +"\nSection Time : "+cursor.getInt(cursor.getColumnIndex("section_time"))
                    +"\nSection Dis : "+cursor.getInt(cursor.getColumnIndex("section_dis"))
                    +"\nStart name : "+cursor.getString(cursor.getColumnIndex("start_name"))
                    +"\nEnd name : "+cursor.getString(cursor.getColumnIndex("end_name"))
                    +"\nWay code : "+cursor.getInt(cursor.getColumnIndex("way_code"))
                    +"\nbus type : "+cursor.getInt(cursor.getColumnIndex("type"))
                    +"\nbus No : "+cursor.getInt(cursor.getColumnIndex("bus_no")));
        }
        cursor.close();

    }
}
