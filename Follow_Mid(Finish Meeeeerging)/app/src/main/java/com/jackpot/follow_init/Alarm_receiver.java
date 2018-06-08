package com.jackpot.follow_init;

/**
 * Created by KWAK on 2018-06-03.
 */

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class Alarm_receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = intent.getStringExtra("13");
        int id = intent.getIntExtra("id", -1);
        Database_overall DB_helper = Database_overall.getInstance(context);

        Cursor cursor = DB_helper.Select();
        while (cursor.moveToNext()){
            if(cursor.getInt(cursor.getColumnIndex("_id")) == id) {    // 리시버가 받은 아이디가 디비의 아이디랑 매칭 -> 디비 수정을 위해
                Log.e("에러", "조건문 진입" + String.valueOf(id));
                if(cursor.getDouble(cursor.getColumnIndex("dept_lati")) == 0.0){    // 수동 알람일 때,
                    Log.e("에러", "수동 진입");
                    DB_helper.Delete(String.valueOf(id));
                } else if (cursor.getInt(cursor.getColumnIndex("weekday")) == 0) {        // 캘린더일 때,
                    Log.e("에러", "캘린더 진입");
                    ContentValues values = new ContentValues();
                    values.put("ala_code", 0);
                    DB_helper.Update(String.valueOf(id), values);
                } else {
                    Log.e("에러", "스케쥴 진입");
                    //스케쥴은 인터벌 두고 반복.
                }
            }
        }
        cursor.close();

       // DB_helper.Delete(String.valueOf(id));
        Intent toService = new Intent(context, Alarm_service.class);
        toService.putExtra("13",key);
        context.startService(toService);
    }
}