package com.jackpot.follow_init;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.eunsiljo.timetablelib.data.TimeData;
import com.github.eunsiljo.timetablelib.data.TimeTableData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

public class FragmentDialog extends DialogFragment {
    View view;

    // Database 객체 생성. (Firebase에서 schedule의 tree를 load.)
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("Schedule");

    public FragmentDialog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_fragment, container, false);
        final Button btnCancel = view.findViewById(R.id.cancelBtn);
        final Button btnSave = view.findViewById(R.id.saveBtn);
        final Button btnArr = view.findViewById(R.id.arrivalBtn);
        final Button btnStart = view.findViewById(R.id.startBtn);

        Bundle args = getArguments();
        String value = args.getString("key");

        fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.plus);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // XML의 widget에 접근하기 위한 구체화.
                EditText editText1 = view.findViewById(R.id.name);
                EditText editText2 = view.findViewById(R.id.inputStart);
                EditText editText3 = view.findViewById(R.id.inputArrival);
                TimePicker timePicker1 = view.findViewById(R.id.startTime);
                TimePicker timePicker2 = view.findViewById(R.id.endTime);

                // Schedule 의 정보를 객체 생성하고 내부에 값 저장.
                obj_schedule data = new obj_schedule();

                // 객체에 이름, 출발, 도착지 저장.
                data.setEvent_name(editText1.getText().toString());
                data.setDept_name(editText2.getText().toString());
                data.setDest_name(editText3.getText().toString());

                // 객체에 time 저장.
                data.setStart_hour(timePicker1.getHour());
                data.setStart_minute(timePicker1.getMinute());
                data.setEnd_hour(timePicker2.getHour());
                data.setEnd_mintue(timePicker2.getMinute());

                // 객체를 DB에 저장.
                databaseReference.push().setValue(data);
            }
        });


        btnArr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    private long getMillis(String day) {
        DateTime date = getDateTimePattern().parseDateTime(day);
        return date.getMillis();
    }

    private DateTimeFormatter getDateTimePattern() {
        return DateTimeFormat.forPattern("HH-mm");
    }
}