package com.jackpot.follow_init;

import android.content.Context;
import android.util.AttributeSet;

import com.p_v.flexiblecalendar.view.CircularEventCellView;

/**
 * Created by KWAK on 2018-05-21.
 */

public class calendar_cellView extends CircularEventCellView {
    public calendar_cellView(Context context) {
        super(context);
    }

    public calendar_cellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public calendar_cellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = (7 * width) / 8;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

