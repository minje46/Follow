package com.jackpot.follow_init;

import android.app.Activity;
import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by KWAK on 2018-05-14.
 */

public class Tutorial extends Fragment {
    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tutorial, container, false);
        mainActivity = (MainActivity) getActivity();

        Button inform = rootView.findViewById(R.id.tutorialInform);
        Button sche = rootView.findViewById(R.id.tutorialSche);
        Button cal = rootView.findViewById(R.id.tutorialCal);
        Button ala = rootView.findViewById(R.id.tutorialAla);

        inform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tutorialInform) {
                    startActivityForResult(new Intent(getActivity(), InformationActivity.class), 1);
                }
            }
        });

        sche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tutorialSche) {
                    startActivityForResult(new Intent(getActivity(), Schedule_Tutorial_Activity.class), 1);
                }
            }
        });

        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tutorialCal) {
                    startActivityForResult(new Intent(getActivity(), Calendar_Tutorial_Activity.class), 1);
                }
            }
        });

        ala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tutorialAla) {
                    startActivityForResult(new Intent(getActivity(), Alarm_Tutorial_Activity.class), 1);
                }
            }
        });

        return rootView;
    }
}