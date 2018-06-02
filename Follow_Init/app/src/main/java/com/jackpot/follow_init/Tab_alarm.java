package com.jackpot.follow_init;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jackpot.follow_init.R;

import static android.media.CamcorderProfile.get;

/**
 * Created by KWAK on 2018-05-14.
 */

public class Tab_alarm extends Fragment { // Main13Activity
    ListView lv;
    Data obj;
    ImageView im;
    ArrayList<String> al = new ArrayList<String>();
    ArrayList<String> ide = new ArrayList<>();
    SQLiteDatabase db;
    AlarmData ad;
    int d_alarmId;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        final ViewGroup rootView =(ViewGroup)inflater.inflate(R.layout.tab_alarm, container, false);

        lv = (ListView)rootView.findViewById(R.id.listView);
        FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag_alarm frag = new frag_alarm();
                getFragmentManager().beginTransaction().replace(R.id.container, frag).commit();
            }
        });


        obj = new Data(rootView.getContext());
        Cursor c = obj.mySelect();
        while(c.moveToNext()) {
            String k = c.getString(0);
            al.add(k);
            String i = c.getString(1);
            ide.add(i);
        }

        final ArrayAdapter<String> aa = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_checked, al);
        lv.setAdapter(aa);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String k = (String)parent.getItemAtPosition(position);

                databaseReference.child("Alarm").child(k).removeValue();

                db = obj.getWritableDatabase();

                db.delete(Data.TABLE_TASK, ide.get(position) + "=" + Data.KEY_ID, null);
                Toast.makeText(rootView.getContext(), k + " Removed", Toast.LENGTH_SHORT).show();

                MainActivity ma = (MainActivity)getActivity();
                ma.deleteAlarm(ma.hm.get(k));
                Log.e("알람", "삭제..." + ma.hm.get(k));

                Tab_alarm frag = new Tab_alarm();
                getFragmentManager().beginTransaction().replace(R.id.container, frag).commit();
                return false;
            }
        });

        return rootView;
    }
}
