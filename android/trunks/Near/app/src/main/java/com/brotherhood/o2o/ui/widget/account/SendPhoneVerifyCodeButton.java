package com.brotherhood.o2o.ui.widget.account;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;

import com.brotherhood.o2o.R;

/**
 * Created with Android Studio.
 */

public class SendPhoneVerifyCodeButton extends Button {

    public SendPhoneVerifyCodeButton(Context context) {
        this(context, null);
    }

    public SendPhoneVerifyCodeButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SendPhoneVerifyCodeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setTextColor(Color.WHITE);
        setAlpha(1f);
        setGravity(Gravity.CENTER);
    }

    /**
     * 获取验证码时间间隔
     */
    private CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            int time = (int) millisUntilFinished / 1000;
            setText(getResources().getString(R.string.updatephone_verify_wait, time));
            setAlpha(0.5f);
        }

        @Override
        public void onFinish() {
            setTag(false);
            init();
        }
    };


    private void init() {
        setText(getResources().getText(R.string.updatephone_get_verifycode));
        setAlpha(1f);
    }

    public void countTimeStart(){
        countDownTimer.start();
    }

    public void countTimeFinish(){
        countDownTimer.onFinish();
    }
}
