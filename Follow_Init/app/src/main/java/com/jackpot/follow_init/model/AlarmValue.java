package com.jackpot.follow_init.model;

/**
 * Created by Yuriy on 11.06.2017.
 */
public interface AlarmValue {
    int getId();

    boolean isEnabled();

    int getHour();

    int getMinutes();

    DaysOfWeek getDaysOfWeek();

    String getLabel();
}