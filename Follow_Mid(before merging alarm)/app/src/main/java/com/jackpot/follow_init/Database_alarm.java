package com.jackpot.follow_init;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database_alarm extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="AlarmDB";
    public static final String KEY_ID="id";
    public static final String TABLE_TASK="alarmList";          // Table_Task 가 Table 의 name 인듯?

    public Database_alarm(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists alarmList(task text,id integer primary key)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TASK);
        onCreate(db);
    }

    public void myInsert(ContentValues cv){
        getWritableDatabase().insert("alarmList", null, cv);
    }

    public Cursor mySelect() {
        Cursor cursor = getReadableDatabase().rawQuery("Select * from alarmList", null);
        return cursor;
    }

}
