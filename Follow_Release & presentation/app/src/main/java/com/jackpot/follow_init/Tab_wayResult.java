package com.jackpot.follow_init;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by KWAK on 2018-06-10.
 */

public class Tab_wayResult extends Fragment{
    private MainActivity mainActivity;
    private ListView listView;
    Context context;

    ArrayList<Obj_search> fileList = new ArrayList<Obj_search>();
    SearchAdapter mAdapter;

    int primarykey;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tab_way_result, container, false);
        context = rootView.getContext();
        mainActivity = (MainActivity)getActivity();
        fileList = new ArrayList<Obj_search>();
        primarykey = mainActivity.id;
        Cursor cursor = mainActivity.DB_result.Select();
        while(cursor.moveToNext()){
            /*
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
                    +"\nbus No : "+cursor.getString(cursor.getColumnIndex("bus_no")));
*/
            if(primarykey == cursor.getInt(cursor.getColumnIndex("fk")))
            {
                Obj_search ex = new Obj_search();
                ex.setSection_time(cursor.getInt(cursor.getColumnIndex("section_time")));
                ex.setSection_distance(cursor.getInt(cursor.getColumnIndex("section_dis")));
                ex.setTrafficType(cursor.getInt(cursor.getColumnIndex("traffic_type")));
                switch (cursor.getInt(cursor.getColumnIndex("traffic_type"))){
                    case 1:
                        ex.setStartName(cursor.getString(cursor.getColumnIndex("start_name")));
                        ex.setEndName(cursor.getString(cursor.getColumnIndex("end_name")));
                        ex.setWayCode(cursor.getInt(cursor.getColumnIndex("way_code")));
                        break;
                    case 2:
                        ex.setStartName(cursor.getString(cursor.getColumnIndex("start_name")));
                        ex.setEndName(cursor.getString(cursor.getColumnIndex("end_name")));
                        ex.setBusNo(cursor.getString(cursor.getColumnIndex("bus_no")));
                        ex.setType(cursor.getInt(cursor.getColumnIndex("type")));
                        break;
                }
                fileList.add(ex);
            }
        }
        cursor.close();
        listView = (ListView)rootView.findViewById(R.id.listview1);
        mAdapter = new SearchAdapter(context, fileList);
        listView.setAdapter(mAdapter);
        return rootView;
    }

    class SearchAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Obj_search> items;

        public SearchAdapter() {}

        public SearchAdapter(Context context, ArrayList<Obj_search> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            Obj_search data = items.get(position);
            switch (data.getTrafficType()){
                case 1: // 1 is subway
                    viewHolder.title.setText(mainActivity.subwayCode.get(data.getWayCode()) + " "+ data.getStartName() + "역");
                    viewHolder.subtitle.setText("" + data.getEndName() + "역 하차");
                    viewHolder.sectionTime.setText(data.getSection_time() + "분");
                    viewHolder.icon.setImageResource(R.drawable.subway); // 수단 이미지로 변경 필요
                    viewHolder.line.setImageResource(R.drawable.line); // 선 이미지로 변경 필요
                    break;
                case 2: // 2 is bus
                    viewHolder.title.setText(data.getBusNo() + "번 " + data.getStartName());
                    viewHolder.subtitle.setText("" + data.getEndName() + " 하차");
                    viewHolder.sectionTime.setText(data.getSection_time() + "분");
                    viewHolder.icon.setImageResource(R.drawable.bus); // 수단 이미지로 변경 필요
                    viewHolder.line.setImageResource(R.drawable.line); // 선 이미지로 변경 필요
                    break;
                case 3: // 3 is walking
                    if (data.getSection_time() == 0) {
                        viewHolder.title.setText("도보");
                        viewHolder.subtitle.setText("환승");
                        viewHolder.sectionTime.setText("3분");
                        viewHolder.icon.setImageResource(R.drawable.walking); // 수단 이미지로 변경 필요
                        viewHolder.line.setImageResource(R.drawable.line); // 선 이미지로 변경 필요
                    }
                    else {
                        viewHolder.title.setText("도보");
                        viewHolder.subtitle.setText(data.getSection_distance() + "m");
                        viewHolder.sectionTime.setText(data.getSection_time() + "분");
                        viewHolder.icon.setImageResource(R.drawable.walking); // 수단 이미지로 변경 필요
                        viewHolder.line.setImageResource(R.drawable.line); // 선 이미지로 변경 필요
                    }
                    break;
            }
            return convertView;
        }

        private class ViewHolder {
            TextView title, subtitle, sectionTime;
            ImageView icon, line;

            public ViewHolder(View view) {
                title = (TextView)view.findViewById(R.id.titleText);
                title.setSelected(true);
                subtitle = (TextView)view.findViewById(R.id.subTitleText);
                subtitle.setSelected(true);
                sectionTime = (TextView)view.findViewById(R.id.timeText);
                icon = (ImageView)view.findViewById(R.id.iconImage);
                line = (ImageView)view.findViewById(R.id.lineImage);
            }
        }
    }
}