package com.jackpot.follow_init;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;

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

public class Runnable_waySearch implements Runnable {

    private ODsayService odsayService;
    private JSONObject jsonObject;

    private HashMap<Integer,String> busType = new HashMap<Integer, String>();

    private Context mContext;
    private Obj_schedule fromTab_schedule;

    private double time = 0;
    private double t_dis = 0;
    private double w_dis = 0;

    Handler handler;
    Runnable_waySearch runnable_waySearch;

    public Runnable_waySearch(Context context){
        this.mContext = context;
    }

    public Runnable_waySearch(Context context, Obj_schedule fromTab){
        this.mContext = context;
        fromTab_schedule = fromTab;
    }

    @Override
    public void run() {
        odsayService = ODsayService.init(mContext, mContext.getString(R.string.odsay_API_Key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        // data 확인, from tab_schedule 객체.
        Log.d("TESTTTTTING 111",fromTab_schedule.getEvent_name()+fromTab_schedule.getDept_name()+fromTab_schedule.getDept_latitude());

        odsayService.requestSearchPubTransPath(String.valueOf(fromTab_schedule.getDept_longitude()), String.valueOf(fromTab_schedule.getDept_latitude()), String.valueOf(fromTab_schedule.getDest_longitude()), String.valueOf(fromTab_schedule.getDest_latitude()), "0", "0", "0", onResultCallbackListener);

       // handler = new Handler_obj();
//        handler.post(runnable_waySearch);
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

                fromTab_schedule.setSectionTime(time);
                fromTab_schedule.setTot_distance(t_dis);
                fromTab_schedule.setWlk_distance(w_dis);

                for(int i = 0; i < P_subPath.length(); i++)
                    Log.d("Service Test  traffic", C_subPath.get(i).toString());

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

/*
                // call back listener 처리되는 마지막 부분.
                Bundle bundle = new Bundle();
                bundle.putSerializable("runnable",fromTab_schedule);

          //      handler = new Handler_obj();
           //     handler.post(runnable_waySearch);

                // Frag로 바로 data 전달 해보기.
                Fragment fragment = new Tab_alarm();
                fragment.setArguments(bundle);
*/

        handler = new Handler_obj();
        handler.post(runnable_waySearch);


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





