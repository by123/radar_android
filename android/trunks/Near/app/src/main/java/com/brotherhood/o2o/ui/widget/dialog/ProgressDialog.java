package com.brotherhood.o2o.ui.widget.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.brotherhood.o2o.R;

/**
 * 菊花弹框
 */
public class ProgressDialog extends AlertDialog {

    private LayoutInflater mInflater;
    private View mDialogView;
    private Context mContext;

    public ProgressDialog(Context context) {
        super(context, R.style.TranspantDialogStyle);
        this.mContext = context;
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
        mDialogView = mInflater.inflate(R.layout.dialog_progress_view, null);
        setContentView(mDialogView);
    }
}

