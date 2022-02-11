package com.brotherhood.o2o.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.brotherhood.o2o.R;

/**
 * Created by by.huang on 2015/6/4.
 */


public class BasicDialog extends Dialog {

    private OnDialogListener mListener;
    private Activity mContext;
    private TextView mMainTxt;
    private TextView mMinorTxt;
    private Button mConfirmBtn;
    private View mDivideView;
    private Button mCancelBtn;
    private View rootView;

    public interface OnDialogListener {
        void OnConfirm(BasicDialog dialog);

        void OnCancel(BasicDialog dialog);
    }

    public BasicDialog(Activity context, OnDialogListener listener) {
        super(context, R.style.BasicDialog);
        mListener = listener;
        mContext = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_basic, null);
        setContentView(rootView);
        setParams(mContext);
        initView();
    }

    public BasicDialog setMainTxt(String text) {
        mMainTxt.setText(text);
        return this;
    }

    public BasicDialog setMinorTxt(String text) {
        mMinorTxt.setText(text);
        return this;
    }

    public BasicDialog hideMinorTxt()
    {
        mMinorTxt.setVisibility(View.GONE);
        return this;
    }

    public BasicDialog setConfirmTxt(String text) {
        mConfirmBtn.setText(text);
        return this;
    }

    public BasicDialog setCancelTxt(String text) {
        mCancelBtn.setText(text);
        return this;
    }

    public BasicDialog setConfirmTxtColor(int color) {
        mConfirmBtn.setTextColor(color);
        return this;
    }

    public BasicDialog setCancelTxtColor(int color) {
        mCancelBtn.setTextColor(color);
        return this;
    }

    public BasicDialog hideOneButton() {
        mCancelBtn.setVisibility(View.GONE);
        mDivideView.setVisibility(View.GONE);
        return this;
    }

    public BasicDialog hideAllButton() {
        hideOneButton();
        mCancelBtn.setVisibility(View.GONE);
        return this;
    }

    private void initView() {
        mMainTxt = (TextView) findViewById(R.id.txt_main);
        mMinorTxt = (TextView) findViewById(R.id.txt_minor);
        mConfirmBtn = (Button) findViewById(R.id.btn_confirm);
        mCancelBtn = (Button) findViewById(R.id.btn_cancel);
        mDivideView = findViewById(R.id.divide_view);

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener == null) {
                    dismiss();
                } else {
                    mListener.OnConfirm(BasicDialog.this);
                }
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener == null) {
                    dismiss();
                } else {
                    mListener.OnCancel(BasicDialog.this);
                }
            }
        });
    }

    private void setParams(Activity context) {
        Window dialogWindow = getWindow();
        dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth());
        dialogWindow.setAttributes(p);
    }

}
