package com.teddoll.fitness.intervaltrainer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.teddoll.fitness.intervaltrainer.R;

/**
 * Created by teddydoll on 6/12/16.
 */
public class MaxHeightListView extends ListView {
    public MaxHeightListView(Context context) {
        super(context);
        setMaxHeight();
    }

    public MaxHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMaxHeight();
    }

    public MaxHeightListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMaxHeight();
    }

    private int mMaxHeight;
    private void setMaxHeight() {
        mMaxHeight = getResources().getDimensionPixelSize(R.dimen.detail_max_height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
