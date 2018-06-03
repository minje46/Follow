package com.jackpot.follow_init;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by KWAK on 2018-05-30.
 */

public class Handler_obj extends Handler {

    public Handler_obj(){
    }

    public void handleMessage(Message msg){
        Bundle bundle = msg.getData();

        String name = bundle.getString("name");
        Double time = bundle.getDouble("sectionTime");

        Log.d("TesT32342324", name+"\n"+String.valueOf(time));
    }
}
