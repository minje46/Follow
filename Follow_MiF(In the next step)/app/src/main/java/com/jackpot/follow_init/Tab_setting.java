package com.jackpot.follow_init;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by KWAK on 2018-05-14.
 */

public class Tab_setting extends Fragment{
    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tab_setting, container, false);
        mainActivity = (MainActivity)getActivity();


        Cursor cursor = mainActivity.DB_result.Select();
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

        return rootView;
    }
}
