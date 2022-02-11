package com.brotherhood.o2o.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brotherhood.o2o.R;


/**
 * Created by ZhengYi on 15/6/4.
 */
public class ProgressButton extends FrameLayout {

    private static final int DECORATION_PINK = 1;
    private static final int DECORATION_RED = 2;

    private static final int SHAPE_SQUARE = 1;
    private static final int SHAPE_ROUND = 2;

    TextView mTitleLabel;
    ProgressBar mProgressBar;

    public ProgressButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.widget_progress_button, this, true);
        bindSubViews();
        initView(attrs);
    }

    public void setText(CharSequence text) {
        mTitleLabel.setText(text);
    }

    public void setTextColor(int color) {
        mTitleLabel.setTextColor(color);
    }

    public void setTextSize(int unit, float size) {
        mTitleLabel.setTextSize(unit, size);
    }

    public void setIsProcessing(boolean isProcessing) {
        mProgressBar.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
        setClickable(!isProcessing);
    }

    @Override
    public void setOnClickListener(final OnClickListener listener) {
        super.setOnClickListener(listener);
    }

    private void bindSubViews() {
        mTitleLabel = (TextView) findViewById(R.id.widget_titleLabel);
        mProgressBar = (ProgressBar) findViewById(R.id.widget_progress);
    }

    private void initView(@Nullable AttributeSet attrsOrNil) {
        if (attrsOrNil != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrsOrNil, R.styleable.ProgressButton);
            int decoration = typedArray.getInt(R.styleable.ProgressButton_decoration, DECORATION_PINK);
            int shape = typedArray.getInt(R.styleable.ProgressButton_shape, SHAPE_ROUND);
            String textOrNil = typedArray.getString(R.styleable.ProgressButton_text);
            ColorStateList textColorOrNil = typedArray.getColorStateList(R.styleable.ProgressButton_textColor);
            int textSizeOrNil = typedArray.getDimensionPixelSize(R.styleable.ProgressButton_textSize, -1);
            boolean isProcessing = typedArray.getBoolean(R.styleable.ProgressButton_isProcessing, true);

            if (decoration == DECORATION_PINK) {
                mTitleLabel.setTextColor(getResources().getColor(android.R.color.white));
                if (shape == SHAPE_ROUND) {
                    setBackgroundResource(R.drawable.sel_button_round_pink);
                } else if (shape == SHAPE_SQUARE) {
                    setBackgroundResource(R.drawable.sel_button_square_pink);
                }
            } else if (decoration == DECORATION_RED) {
                mTitleLabel.setTextColor(getResources().getColor(android.R.color.white));
                if (shape == SHAPE_ROUND) {
                    setBackgroundResource(R.drawable.sel_button_round_red);
                } else if (shape == SHAPE_SQUARE) {
                    setBackgroundResource(R.drawable.sel_button_square_red);
                }
            }

            if (!TextUtils.isEmpty(textOrNil)) mTitleLabel.setText(textOrNil);
            if (textColorOrNil != null) mTitleLabel.setTextColor(textColorOrNil);
            if (textSizeOrNil != -1)
                mTitleLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeOrNil);
            mProgressBar.setVisibility(isProcessing ? View.VISIBLE : View.GONE);

            typedArray.recycle();
        }
    }
}
