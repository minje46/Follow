package com.jackpot.follow;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.daum.mf.map.api.MapView;

/**
 * Created by KWAK on 2018-05-07.
 */

public class Tab_setting extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        ViewGroup rootView =(ViewGroup)inflater.inflate(R.layout.tab_setting, container, false);

        Button btnMap = (Button)rootView.findViewById(R.id.btnSetting);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.setContentView(R.layout.map);

                // Main activity 가 아닌 외부 activity 의 id 어떻게 참조?
                // MapView 에 들어가는 parameter 값 뭐지?
                /*
                MapView mapView = new MapView(mainActivity.getApplicationContext());
                ViewGroup mapViewContainer = mainActivity.findViewById(R.id.map_view);
                mapViewContainer.addView(mapView);
                */
            }
        });
        return rootView;
    }


}
