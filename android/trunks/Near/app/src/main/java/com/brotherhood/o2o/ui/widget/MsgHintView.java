package com.brotherhood.o2o.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.R;

/**
 * Created by laimo.li on 2015/12/25.
 */
public class MsgHintView extends TextView {

    private TextView tvMsgCount;

    private ImageView ivMsgBg;

    public MsgHintView(Context context) {
        this(context, null);
    }

    public MsgHintView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MsgHintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.setGravity(Gravity.CENTER);
        this.setTextColor(Color.WHITE);
        this.setTextSize(10);

    }

    public void hasMsg(long count) {
        if (count == 0) {
            setVisibility(View.INVISIBLE);
        } else {
            setVisibility(View.VISIBLE);
            if (count > 99) {
                setText("");
                setBackgroundResource(R.mipmap.ic_msg_nametipmore_normal);
            } else {
                setText(String.valueOf(count));
                if (count > 9) {
                    setBackgroundResource(R.mipmap.ic_msg_prompt_normal);
                } else {
                    setBackgroundResource(R.mipmap.ic_msg_nametip_normal);
                }

            }
        }
    }


}
