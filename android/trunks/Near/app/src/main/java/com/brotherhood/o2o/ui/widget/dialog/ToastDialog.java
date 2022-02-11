package com.brotherhood.o2o.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.util.DisplayUtil;

/**
 * Created by laimo.li on 2016/1/13.
 */
public class ToastDialog extends Dialog {

    private View mDialogView;

    private View rootContainer;
    private ImageView ivIcon;
    private TextView tvContent;

    private CountDownTimer currenttDownTimer;

    public ToastDialog(Context context) {
        super(context, R.style.ToastDialogStyle);
        initView();
    }

    private void initView() {
        mDialogView = LayoutInflater.from(getContext()).inflate(R.layout.toast_dialog_view, null);
        rootContainer = mDialogView.findViewById(R.id.container_root);
        rootContainer.getLayoutParams().width = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        rootContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                cancelnewCountDownTimer();
            }
        });
        ivIcon = (ImageView) mDialogView.findViewById(R.id.image_icon);
        tvContent = (TextView) mDialogView.findViewById(R.id.label_content);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialogStyle();
        setContentView(mDialogView);
    }

    private void initDialogStyle() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.width = DisplayUtil.getScreenWidth(getContext());
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.BottomEnterAnim);

    }


    public void show(String color, int iconResId, String msg, int duration) {
        tvContent.setText(msg);
        if (iconResId != -1) {
            ivIcon.setImageResource(iconResId);
        } else {
            ivIcon.setVisibility(View.GONE);
        }
        rootContainer.setBackgroundColor(Color.parseColor(color));

        int showTime = 2;
        if (duration == Toast.LENGTH_SHORT) {
            showTime = 2;
        }else if (duration == Toast.LENGTH_LONG) {
            showTime = 4;
        } else {
            showTime = duration > 0 ? duration : 2;
        }
        cancelnewCountDownTimer();
        newCountDownTimer(showTime);
        if (currenttDownTimer != null) {
            currenttDownTimer.start();
            super.show();
        }
    }

    private void cancelnewCountDownTimer() {
        if (currenttDownTimer != null) {
            currenttDownTimer.cancel();
            currenttDownTimer.onFinish();
            currenttDownTimer = null;
        }
    }

    private void newCountDownTimer(int showTime) {
        currenttDownTimer = new CountDownTimer(showTime * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                dismiss();
            }
        };
    }

}