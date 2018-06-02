package com.jackpot.follow_init;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Set;


/**
 * Created by KWAK on 2018-05-14.
 */

public class Fragment2 extends Fragment {

    private Spinner sp_api;
    private RadioGroup rg_object_type;
    private RadioButton rb_json, rb_map;
    private Button bt_api_call;
    private TextView tv_data;

    private Context context;
    private String spinnerSelectedName;


    private ODsayService odsayService;
    private JSONObject jsonObject;
    private Map mapObject;

    private HashMap<Integer,String> busType = new HashMap<Integer, String>();

    private Double dept_latitude = 37.4507452;
    private Double dept_longitude = 127.1288474;
    private Double dest_latitude = 37.478688;
    private Double dest_longitude = 127.12617499999999;

    public void onCreate(@Nullable Bundle savedInstanceState) { super.onCreate(savedInstanceState);}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment2, container, false);

        context = getActivity();
        sp_api = (Spinner)rootView.findViewById(R.id.sp_api);
        rg_object_type = (RadioGroup)rootView.findViewById(R.id.rg_object_type);
        bt_api_call = (Button) rootView.findViewById(R.id.bt_api_call);
        rb_json = (RadioButton) rootView.findViewById(R.id.rb_json);
        rb_map = (RadioButton)rootView.findViewById(R.id.rb_map);
        tv_data = (TextView) rootView.findViewById(R.id.tv_data);
        sp_api.setSelection(0);

        odsayService = ODsayService.init(context, getString(R.string.odsay_API_Key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        bt_api_call.setOnClickListener(onClickListener);
        sp_api.setOnItemSelectedListener(onItemSelectedListener);
        rg_object_type.setOnCheckedChangeListener(onCheckedChangeListener);
        SetMap_busType();

        // test 위해서 주석 처리. (출발지, 도착지 위,경도 값 저장)
        /*
        dept_latitude = ((MainActivity)getActivity()).getDept_latitude();
        dept_longitude = ((MainActivity)getActivity()).getDept_longitude();
        dest_latitude = ((MainActivity)getActivity()).getDest_latitude();
        dest_longitude = ((MainActivity)getActivity()).getDest_longitude();
        Log.d("Coordinate in Fragment2", String.valueOf(dept_latitude) + "\n" +String.valueOf(dept_longitude) +"\n" +String.valueOf(dest_latitude) + "\n" +String.valueOf(dest_longitude));
        */
        return rootView;
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            if (rg_object_type.getCheckedRadioButtonId() == rb_json.getId()) {
                tv_data.setText(jsonObject.toString());
            } else if (rg_object_type.getCheckedRadioButtonId() == rb_map.getId()) {
                tv_data.setText(mapObject.toString());
            }
        }
    };
    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerSelectedName = (String) parent.getItemAtPosition(position);
            }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            // jsonObject 는 전체 jason data 저장 객체.
            jsonObject = oDsayData.getJson();       // 전체 대중교통 길찾기 data 를 Json 으로 불러오는 것.
            mapObject = oDsayData.getMap();
            if (rg_object_type.getCheckedRadioButtonId() == rb_json.getId()) {
                tv_data.setText(jsonObject.toString());

                Log.e("JSON All", jsonObject.toString());   // Json data 제대로 불러왔는지 log.

                try {
                    // path array 는 path 관련 json data 를 array 형태로 저장.
                    JSONArray path = (JSONArray)jsonObject.getJSONObject("result").get("path");
                    Log.d("Json path", path.toString());    // Path parsing 확인 log.

                    // subPath array 는 path 의 child 중 subPath data 를 array 로 저장.
                    JSONArray P_subPath = (JSONArray)path.getJSONObject(0).get("subPath");
                    Log.d("Json subPath", P_subPath.toString());    // subPath parsing 확인 log.

                    // subPath 의 child 중 traffic 을 저장.
                    ArrayList<JSONObject> C_subPath = new ArrayList<JSONObject>();
                    for(int i = 0; i < P_subPath.length(); i++)
                        C_subPath.add(i,P_subPath.getJSONObject(i));

                    for(int i = 0; i < P_subPath.length(); i++)
                        Log.d("Json traffics", C_subPath.get(i).toString());

                    Log.d("Json traffic0 - Type", String.valueOf(C_subPath.get(0).getInt("trafficType")));
                    Log.d("Json traffic0 - Time", String.valueOf(C_subPath.get(0).getInt("sectionTime")));

                    Log.d("Json traffic1 - BusNo", C_subPath.get(1).getJSONArray("lane").getJSONObject(0).getString("busNo").toString());
                    Log.d("Json traffic1 - BusType", String.valueOf(C_subPath.get(1).getJSONArray("lane").getJSONObject(0).getInt("type")));

                    Log.d("Json traffic2", C_subPath.get(2).toString());

                    if(busType.containsKey(C_subPath.get(1).getJSONArray("lane").getJSONObject(0).getInt("type")))
                        Log.d("Json Type compare",busType.get(C_subPath.get(1).getJSONArray("lane").getJSONObject(0).getInt("type")));
                    else
                        Log.d("Json Type compare", "Not exist");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (rg_object_type.getCheckedRadioButtonId() == rb_map.getId()) {
                tv_data.setText(mapObject.toString());
            }

        }

        @Override
        public void onError(int i, String errorMessage, API api) {
            tv_data.setText("API : " + api.name() + "\n" + errorMessage);
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (spinnerSelectedName) {
                case "버스 노선 조회":
                    odsayService.requestSearchBusLane("150", "1000", "no", "10", "1", onResultCallbackListener);
                    break;
                case "버스노선 상세정보 조회":
                    odsayService.requestBusLaneDetail("12018", onResultCallbackListener);
                    break;
                case "버스정류장 세부정보 조회":
                    odsayService.requestBusStationInfo("107475", onResultCallbackListener);
                    break;
                case "열차•KTX 운행정보 검색":
                    odsayService.requestTrainServiceTime("3300128", "3300108", onResultCallbackListener);
                    break;
                case "고속버스 운행정보 검색":
                    odsayService.requestExpressServiceTime("4000057", "4000030", onResultCallbackListener);
                    break;
                case "시외버스 운행정보 검색":
                    odsayService.requestIntercityServiceTime("4000022", "4000255", onResultCallbackListener);
                    break;
                case "항공 운행정보 검색":
                    odsayService.requestAirServiceTime("3500001", "3500003", "6", onResultCallbackListener);
                    break;
                case "운수회사별 버스노선 조회":
                    odsayService.requestSearchByCompany("792", "100", onResultCallbackListener);
                    break;
                case "지하철역 세부 정보 조회":
                    odsayService.requestSubwayStationInfo("130", onResultCallbackListener);
                    break;
                case "지하철역 전체 시간표 조회":
                    odsayService.requestSubwayTimeTable("130", "1", onResultCallbackListener);
                    break;
                case "노선 그래픽 데이터 검색":
                    odsayService.requestLoadLane("0:0@12018:1:-1:-1", onResultCallbackListener);
                    break;
                case "대중교통 정류장 검색":
                    odsayService.requestSearchStation("11", "1000", "1:2", "10", "1", "127.0363583:37.5113295", onResultCallbackListener);
                    break;
                case "반경내 대중교통 POI 검색":
                    odsayService.requestPointSearch("126.933361407195", "37.3643392278118", "250", "1:2", onResultCallbackListener);
                    break;
                case "지도 위 대중교통 POI 검색":
                    odsayService.requestBoundarySearch("127.045478316811:37.68882830829:127.055063420699:37.6370465749586", "127.045478316811:37.68882830829:127.055063420699:37.6370465749586", "1:2", onResultCallbackListener);
                    break;
                case "지하철 경로검색 조회(지하철 노선도)":
                    odsayService.requestSubwayPath("1000", "201", "222", "1", onResultCallbackListener);
                    break;
                case "대중교통 길찾기":
//                    odsayService.requestSearchPubTransPath("126.926493082645", "37.6134436427887", "127.126936754911", "37.5004198786564", "0", "0", "0", onResultCallbackListener);
                    odsayService.requestSearchPubTransPath(dept_longitude.toString(), dept_latitude.toString(), dest_longitude.toString(), dest_latitude.toString(),"0", "0", "0", onResultCallbackListener);
                    break;
                case "지하철역 환승 정보 조회":
                    odsayService.requestSubwayTransitInfo("133", onResultCallbackListener);
                    break;
                case "고속버스 터미널 조회":
                    odsayService.requestExpressBusTerminals("1000", "서울", onResultCallbackListener);
                    break;
                case "시외버스 터미널 조회":
                    odsayService.requestIntercityBusTerminals("1000", "서울", onResultCallbackListener);
                    break;
                case "도시코드 조회":
                    odsayService.requestSearchCID("서울", onResultCallbackListener);
                    break;
            }
        }
    };

    private void SetMap_busType(){
        busType.put(1,"일반"); busType.put(2,"좌석"); busType.put(3,"마을버스");
        busType.put(4,"직행좌석"); busType.put(5,"공항버스"); busType.put(6,"간선급행");
        busType.put(10,"외곽"); busType.put(11,"간선"); busType.put(12,"지선");
        busType.put(13,"순환"); busType.put(14,"광역"); busType.put(15,"급행");
        busType.put(20,"농어촌버스"); busType.put(21,"제주도 시외형버스"); busType.put(22,"경기도 시외형버스");
        busType.put(26, "급행간선");
    }
}
