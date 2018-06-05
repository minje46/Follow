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

    private HashMap<Integer,String> busType = new HashMap<Integer, String>();

    private Context mContext;
    private int primaryKey;

    private double time = 0;
    private double t_dis = 0;
    private double w_dis = 0;

    SQLiteDatabase database;
    Database_overall DB_helper;

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
/*
        while(cursor.moveToNext()){     // DB 내부 Test Log. (지우지 말 것.)
            Log.e("데이터베이스in쓰레드","ID : "+cursor.getInt(cursor.getColumnIndex("_id"))
                    +"\nEvent name : "+cursor.getString(cursor.getColumnIndex("event_name"))
                    +"\nDept name : "+cursor.getString(cursor.getColumnIndex("dept_name"))
                    +"\nDept Lati : "+cursor.getDouble(cursor.getColumnIndex("dept_lati"))
                    +"\nDest name : "+cursor.getString(cursor.getColumnIndex("dest_name"))
                    +"\nDest Long : "+cursor.getDouble(cursor.getColumnIndex("dest_long"))
                    +"\nSect time : "+cursor.getInt(cursor.getColumnIndex("sec_time"))
                    +"\nStart hour : "+cursor.getInt(cursor.getColumnIndex("str_hour"))
                    +"\nEnd minute : "+cursor.getInt(cursor.getColumnIndex("end_min"))
                    +"\nAlarm hour : "+cursor.getInt(cursor.getColumnIndex("ala_hour"))
                    +"\nAlarm minute : "+cursor.getInt(cursor.getColumnIndex("ala_min")));
        }
*/
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
            // jsonObject 는 전체 jason data 저장 객체.
            jsonObject = oDsayData.getJson();       // 전체 대중교통 길찾기 data 를 Json 으로 불러오는 것.
            Log.e("Service Test JSON All", jsonObject.toString());   // Json data 제대로 불러왔는지 log.

            try {
                // path array 는 path 관련 json data 를 array 형태로 저장.
                JSONArray path = (JSONArray)jsonObject.getJSONObject("result").get("path");
                Log.d("Service Test Json path", path.toString());    // Path parsing 확인 log.

                // subPath array 는 path 의 child 중 subPath data 를 array 로 저장.
                JSONArray P_subPath = (JSONArray)path.getJSONObject(0).get("subPath");
                Log.d("Service Test subPath", P_subPath.toString());    // subPath parsing 확인 log.

                // subPath 의 child 중 traffic 을 저장.
                ArrayList<JSONObject> C_subPath = new ArrayList<JSONObject>();
                for(int i = 0; i < P_subPath.length(); i++) {
                    C_subPath.add(i, P_subPath.getJSONObject(i));
                    time += C_subPath.get(i).getInt("sectionTime");

                    t_dis += C_subPath.get(i).getInt("distance");

                    if(C_subPath.get(i).getInt("trafficType") == 3)
                        w_dis += C_subPath.get(i).getInt("distance");
                }

                for(int i = 0; i < P_subPath.length(); i++)
                    Log.d("Service Test  traffic", C_subPath.get(i).toString());


                // 길찾기 결과 값 Main db에 값저장.
                database = DB_helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("sec_time", time);
                values.put("tot_dis",t_dis);
                values.put("wak_dis",w_dis);
                values.put("ala_code", 1);
                database.update("dataTable", values, "_id=?", new String[]{String.valueOf(primaryKey)});

                Cursor cursor = DB_helper.Select();
                cursor.moveToLast();

                // 길찾기 완료 후, Tab_alarm 호출. (For setting alarm automatically)
//                ((MainActivity)mContext).F_alarm.setAlarm(DB_helper, primaryKey, mContext);
                ((MainActivity)mContext).F_alarm.setAlarm(mContext, primaryKey);
                // Test 다시 해보기. 몇개 결과값 JSON에서 제대로 안뽑아옴.
                Log.d("Service Test tot_time ",String.valueOf(time));
                Log.d("Service Test tot_dis ",String.valueOf(t_dis));
                Log.d("Service Test tot_wdis ",String.valueOf(w_dis));

                Log.d("Service Test - Type", String.valueOf(C_subPath.get(0).getInt("trafficType")));
                Log.d("Service Test - Time", String.valueOf(C_subPath.get(0).getInt("sectionTime")));
                Log.d("Service Test - BusNo", C_subPath.get(1).getJSONArray("lane").getJSONObject(0).getString("busNo").toString());
                Log.d("Service Test - BusType", String.valueOf(C_subPath.get(1).getJSONArray("lane").getJSONObject(0).getInt("type")));

                Log.d("Service Test traffic2", C_subPath.get(2).toString());

                if(busType.containsKey(C_subPath.get(1).getJSONArray("lane").getJSONObject(0).getInt("type")))
                    Log.d("Service Test  compare",busType.get(C_subPath.get(1).getJSONArray("lane").getJSONObject(0).getInt("type")));
                else
                    Log.d("Service Test compare", "Not exist");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(int i, String errorMessage, API api) {
            Log.d("Service Test", "Result call back fail");
        }
    };
}
