package com.jackpot.follow_init;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WayService extends Service {

    private ODsayService odsayService;
    private JSONObject jsonObject;

    private HashMap<Integer,String> busType = new HashMap<Integer, String>();

    private Double dept_latitude = 37.4507452;
    private Double dept_longitude = 127.1288474;
    private Double dest_latitude = 37.478688;
    private Double dest_longitude = 127.12617499999999;

    public WayService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service Test", "Service turns on");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //if(intent == null)
        if(intent.getIntExtra("check",0) == 0)
        {
            Log.d("Service Test", "Intent is null");
            return Service.START_STICKY;
        }else{
            processCommand(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                for(int i = 0; i < P_subPath.length(); i++)
                    C_subPath.add(i,P_subPath.getJSONObject(i));

                for(int i = 0; i < P_subPath.length(); i++)
                    Log.d("Service Test  traffic", C_subPath.get(i).toString());

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
    private void processCommand(Intent intent){

        odsayService = ODsayService.init(getApplicationContext(), getString(R.string.odsay_API_Key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        odsayService.requestSearchPubTransPath(dept_longitude.toString(), dept_latitude.toString(), dest_longitude.toString(), dest_latitude.toString(),"0", "0", "0", onResultCallbackListener);
    }

}
