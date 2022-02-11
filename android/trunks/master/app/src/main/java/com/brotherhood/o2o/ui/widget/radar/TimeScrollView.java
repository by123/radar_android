package com.brotherhood.o2o.ui.widget.radar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

/**
 * Created by by.huang on 2015/6/26.
 */
public class TimeScrollView extends LinearLayout {

    private ScrollText mMiniteTxt = null;
    private ScrollText mSecondTxt = null;

    public TimeScrollView(Context context) {
        super(context);
        initView(context);
    }

    public TimeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TimeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
        mMiniteTxt = new ScrollText(context);
        mSecondTxt = new ScrollText(context);

        addView(mMiniteTxt);
        addView(mSecondTxt);
    }

    public void start() {
        mMiniteTxt.start();
        mSecondTxt.start();
    }
}
