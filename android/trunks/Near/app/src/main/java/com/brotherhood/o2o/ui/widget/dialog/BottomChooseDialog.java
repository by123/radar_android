package com.brotherhood.o2o.ui.widget.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.util.DisplayUtil;

/**
 * 底部三行选择弹框
 */

public class BottomChooseDialog extends AlertDialog{

    private LayoutInflater mInflater;
    private View mDialogView;
    private Context mContext;
    private int mType;
    private View.OnClickListener mOnClickListener;

    public BottomChooseDialog(Context context, int type) {
        super(context, R.style.MyDialogStyle);
        this.mContext = context;
        this.mType = type;
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialogStyle();
        switch (mType){
            case DialogType.PHOTO_CHOOSE://照片选择
                mDialogView = mInflater.inflate(R.layout.dialog_photo_choose_view, null);
                break;
            case DialogType.SEX_CHOOSE://性别选择
                mDialogView = mInflater.inflate(R.layout.dialog_sex_choose_view, null);
                break;
            case DialogType.FRIEND_OPERATE://好友操作
                mDialogView = mInflater.inflate(R.layout.dialog_friend_operate_view, null);
                break;
            case DialogType.NOT_FRIEND_OPERATE://好友操作
                mDialogView = mInflater.inflate(R.layout.dialog_friend_operate_view, null);
                break;
            case DialogType.LOGOUT: //退出登陆
                mDialogView = mInflater.inflate(R.layout.dialog_logout_view, null);
                break;
            case DialogType.EMPTY_GROUP_CHAT:
                mDialogView = mInflater.inflate(R.layout.dialog_empty_group_chat_view, null);
                break;
            case DialogType.EMAIL_REGISTED:
                mDialogView = mInflater.inflate(R.layout.dialog_email_registed_view, null);
                break;

        }
        setContentView(mDialogView);
        if (mType == DialogType.NOT_FRIEND_OPERATE){
            mDialogView.findViewById(R.id.viDialogFirstDivide).setVisibility(View.GONE);
            mDialogView.findViewById(R.id.dialogFirstLine).setVisibility(View.GONE);
        }
        initEvent();
    }

    private void initDialogStyle() {
        Window window = getWindow();
        window.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DisplayUtil.getScreenWidth(mContext); //设置宽度为全屏
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.BottomEnterAnim); //设置窗口弹出动画
    }


    private void initEvent() {
        if (mOnClickListener == null){
            return;
        }
        if (mType != DialogType.SEX_CHOOSE) {
            mDialogView.findViewById(R.id.dialogFirstLine).setOnClickListener(mOnClickListener);
        }
        mDialogView.findViewById(R.id.dialogSecondLine).setOnClickListener(mOnClickListener);
        mDialogView.findViewById(R.id.dialogThirdLine).setOnClickListener(mOnClickListener);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public interface DialogType {
        public static final int PHOTO_CHOOSE = 1;
        public static final int SEX_CHOOSE = 2;
        public static final int FRIEND_OPERATE = 3;
        public static final int NOT_FRIEND_OPERATE = 4;
        public static final int LOGOUT = 5;
        public static final int EMPTY_GROUP_CHAT = 6;
        public static final int EMAIL_REGISTED = 7;

    }

}

