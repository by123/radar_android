package com.brotherhood.o2o.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;

import com.brotherhood.o2o.R;

/**
 * Created by ZhengYi on 15/6/4.
 */
public class DecorationButton extends Button {
    private static final int DECORATION_PINK = 1;
    private static final int DECORAtION_RED = 2;
    private static final int SHAPE_SQUARE = 1;
    private static final int SHAPE_ROUND = 2;

    public DecorationButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DecorationButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrsOrNil) {
        setGravity(Gravity.CENTER);
        if (attrsOrNil != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrsOrNil, R.styleable.DecorationButton);
            int decoration = typedArray.getInt(R.styleable.DecorationButton_decoration, DECORATION_PINK);
            int shape = typedArray.getInt(R.styleable.DecorationButton_shape, SHAPE_SQUARE);

            if (decoration == DECORATION_PINK) {
                setTextColor(getResources().getColor(android.R.color.white));
                if (shape == SHAPE_SQUARE) {
                    setBackgroundResource(R.drawable.sel_button_square_pink);
                } else if (shape == SHAPE_ROUND) {
                    setBackgroundResource(R.drawable.sel_button_round_pink);
                }
            } else if (decoration == DECORAtION_RED) {
                setTextColor(getResources().getColor(android.R.color.white));
                if (shape == SHAPE_SQUARE) {
                    setBackgroundResource(R.drawable.sel_button_square_red);
                } else if (shape == SHAPE_ROUND) {
                    setBackgroundResource(R.drawable.sel_button_round_red);
                }
            }
            typedArray.recycle();
        }
    }
}
