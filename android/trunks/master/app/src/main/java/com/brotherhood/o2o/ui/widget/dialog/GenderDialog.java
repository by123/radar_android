package com.brotherhood.o2o.ui.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.brotherhood.o2o.R;

/**
 * Created by by.huang on 2015/7/20.
 */
public class GenderDialog extends Dialog implements View.OnClickListener {

    private Activity mContext;
    private View mMaleBtn;
    private View mFemaleBtn;
    private OnGenderDialogListener mListener;

    public GenderDialog(Activity context, OnGenderDialogListener listener) {
        super(context, R.style.BasicDialog);
        mContext = context;
        this.mListener = listener;
        setContentView(R.layout.dialog_gender);
        setCancelable(true);
        setParams(mContext);
        initView();
    }

    private void initView() {
        mMaleBtn =  findViewById(R.id.btn_male);
        mFemaleBtn =  findViewById(R.id.btn_female);

        mMaleBtn.setOnClickListener(this);
        mFemaleBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == mMaleBtn) {
            if (mListener != null) {
                mListener.OnMaleClick();
            }

        } else if (view == mFemaleBtn) {
            if (mListener != null) {
                mListener.OnFemaleClick();
            }
        }
        dismiss();
    }


    private void setParams(Activity context) {
        Window dialogWindow = getWindow();
//        dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth());
        dialogWindow.setAttributes(p);
    }

    public interface OnGenderDialogListener {
        void OnMaleClick();

        void OnFemaleClick();
    }
}
