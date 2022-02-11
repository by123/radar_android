package com.brotherhood.o2o.ui.widget.nearby;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.util.ViewUtil;

/**
 * Created by jl.zhang on 2015/12/29.
 */
public class FoodScoreView extends TextView {

    private Context mContext;

    public FoodScoreView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public FoodScoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FoodScoreView(Context context) {
        this(context, null);
    }

    public void setScore(String value){
        float score = Float.valueOf(value);
        setText(String.valueOf(score));
        if (score <= 6){
            ViewUtil.setViewBackground(this, R.drawable.food_score_darkyellow_bg);
        }else if (score <= 7){
            ViewUtil.setViewBackground(this, R.drawable.food_score_yellow_bg);
        }else if (score <= 8){
            ViewUtil.setViewBackground(this, R.drawable.food_score_hollogreen_bg);
        }else if (score <= 9){
            ViewUtil.setViewBackground(this, R.drawable.food_score_normalreen_bg);
        }else if (score <= 10){
            ViewUtil.setViewBackground(this, R.drawable.food_score_darkgreen_bg);
        }
    }

}
