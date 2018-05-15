package com.jackpot.follow_init.model;

/**
 * Created by Yuriy on 08.07.2017.
 */

public interface AlarmChangeData extends  AlarmValue {
    boolean isPrealarm();

    String alertString();

    boolean isVibrate();
}
