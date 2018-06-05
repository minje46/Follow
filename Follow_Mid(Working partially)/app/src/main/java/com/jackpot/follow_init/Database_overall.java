package com.jackpot.follow_init;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by KWAK on 2018-06-03.
 */

public class Database_overall extends SQLiteOpenHelper implements Serializable {

    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="OverallDB";

    private static volatile Database_overall helper;

    // Using singleton to access database everywhere thou, idk about singleton at all.
    static Database_overall getInstance(Context context){
        if(helper == null){
            synchronized (Database_overall.class){
                if(helper == null){
                    helper = new Database_overall(context, null);
                }
            }
        }
        return helper;
    }

    public Database_overall(Context context, SQLiteDatabase.CursorFactory factory){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

        Log.e("데이터베이스", "디비 내부임");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "Create table dataTable ("+
                    "_id integer primary key autoincrement,"+
                    "event_name text,"+
                    "dept_name text,"+
                    "dest_name text,"+
                    "dept_lati double,"+
                    "dept_long double,"+
                    "dest_lati double,"+
                    "dest_long double,"+
                    "sec_time double,"+
                    "tot_dis double,"+
                    "wak_dis double,"+
                    "year integer,"+
                    "month integer,"+
                    "day integer,"+
                    "weekday integer,"+
                    "str_hour integer,"+
                    "str_min integer,"+
                    "end_hour integer,"+
                    "end_min integer,"+
                    "ala_hour integer,"+
                    "ala_min integer,"+
                    "ala_code integer);";
        db.execSQL(sql);

        Log.e("데이터베이스2", "생성됬어염 쀼");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "Drop table if exists dataTable";
        db.execSQL(sql);
        onCreate(db);
    }

    public void Insert(ContentValues cv){
        getWritableDatabase().insert("dataTable", null, cv);
    }

    public Cursor Select() {
        Cursor cursor = getReadableDatabase().rawQuery("Select * from dataTable", null);
        return cursor;
    }

}