package com.jackpot.follow_init;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by KWAK on 2018-06-09.
 */

public class Database_search extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="Result";

    public Database_search(Context context, SQLiteDatabase.CursorFactory factory){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

        Log.e("데이터베이스2", "디비 내부임2");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "Create table dataResult ("+
                "_id integer primary key autoincrement,"+
                "fk integer,"+
                "path_type integer,"+
                "traffic_type integer,"+
                "section_time integer,"+
                "section_dis integer,"+
                "start_name text,"+
                "end_name text,"+
                "way_code integer,"+
                "bus_no integer,"+
                "type integer);";

        db.execSQL(sql);

        Log.e("데이터베이스2", "생성됬어염 쀼2");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "Drop table if exists dataTable";
        db.execSQL(sql);
        onCreate(db);
    }

    public void Insert(ContentValues cv){
        getWritableDatabase().insert("dataResult", null, cv);
    }

    public void Delete(String id) {
        getWritableDatabase().delete("dataResult", "_id=?", new String[]{id});
    }

    public void Update(String id, ContentValues values) {
        getWritableDatabase().update("dataResult", values, "_id=?", new String[]{id});
    }

    public Cursor Select() {
        Cursor cursor = getReadableDatabase().rawQuery("Select * from dataResult", null);
        return cursor;
    }
}