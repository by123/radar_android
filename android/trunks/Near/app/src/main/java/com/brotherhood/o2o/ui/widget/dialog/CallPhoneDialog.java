package com.brotherhood.o2o.ui.widget.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.brotherhood.o2o.R;

/**
 * Created by jl.zhang on 2015/12/31.
 */
public class CallPhoneDialog extends AlertDialog implements View.OnClickListener{

    private Context mContext;
    private LayoutInflater mInflater;
    private View mDialogView;
    private TextView mTvTitle;
    private String mPhoneNo;

    public CallPhoneDialog(Context context) {
        super(context, R.style.MyDialogStyle);
        this.mContext = context;
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialogStyle();
        mDialogView = mInflater.inflate(R.layout.call_phone_dialog, null);
        setContentView(mDialogView);
        mTvTitle = (TextView) mDialogView.findViewById(R.id.tvDialogPhoneNumber);
        if (!TextUtils.isEmpty(mPhoneNo)){
            mTvTitle.setText(mPhoneNo);
        }
        initEvent();
    }

    public void setPhoneNumber(String phoneNo){
        this.mPhoneNo = phoneNo;
    }

    private void initDialogStyle() {
        Window window = getWindow();
        window.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
    }


    private void initEvent() {
        mDialogView.findViewById(R.id.tvDialogCallPhone).setOnClickListener(this);
        mDialogView.findViewById(R.id.tvDialogCancelCall).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvDialogCallPhone://用intent启动拨打电话
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhoneNo));
                mContext.startActivity(intent);
                break;
        }
        dismiss();
    }
}
