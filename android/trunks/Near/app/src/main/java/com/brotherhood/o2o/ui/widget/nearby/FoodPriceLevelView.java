package com.brotherhood.o2o.ui.widget.nearby;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;

/**
 * Created by jl.zhang on 2015/12/29.
 */
public class FoodPriceLevelView extends LinearLayout {

    private Context mContext;
    private TextView mTvLevelOne;
    private TextView mTvLevelTwo;
    private TextView mTvLevelThree;
    private TextView mTvLevelFour;

    public FoodPriceLevelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public FoodPriceLevelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FoodPriceLevelView(Context context) {
        this(context, null);
    }

    private void init() {
        View.inflate(mContext, R.layout.food_price_level_view, this);
        mTvLevelOne = (TextView) findViewById(R.id.tvPriceLevelOne);
        mTvLevelTwo = (TextView) findViewById(R.id.tvPriceLevelTwo);
        mTvLevelThree = (TextView) findViewById(R.id.tvPriceLevelThree);
        mTvLevelFour = (TextView) findViewById(R.id.tvPriceLevelFour);
    }

    public void setLevel(int level, String unit){
        mTvLevelOne.setText(unit);
        mTvLevelTwo.setText(unit);
        mTvLevelThree.setText(unit);
        mTvLevelFour.setText(unit);
        switch (level){
            case 1:
                mTvLevelTwo.setTextColor(getResources().getColor(R.color.thirty_percent_black));
                mTvLevelThree.setTextColor(getResources().getColor(R.color.thirty_percent_black));
                mTvLevelFour.setTextColor(getResources().getColor(R.color.thirty_percent_black));
                break;
            case 2:
                mTvLevelThree.setTextColor(getResources().getColor(R.color.thirty_percent_black));
                mTvLevelFour.setTextColor(getResources().getColor(R.color.thirty_percent_black));
                break;
            case 3:
                mTvLevelFour.setTextColor(getResources().getColor(R.color.thirty_percent_black));
                break;
            case 4:
                break;
        }
    }
}
