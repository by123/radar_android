package com.brotherhood.o2o.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;

import com.brotherhood.o2o.ui.widget.dialog.BasicDialog;

/**
 * Created by ZhengYi on 15/6/2.
 */
public class DialogHelper {
    private DialogHelper() {
    }

    public static void showSimpleAlertDialog(Activity activity, String message) {
        BasicDialog dialog = new BasicDialog(activity, new BasicDialog.OnDialogListener() {
            @Override
            public void OnConfirm(BasicDialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void OnCancel(BasicDialog dialog) {
            }
        });
        dialog.setCancelable(false);
        dialog.setMainTxt(message);
        dialog.hideMinorTxt();
        dialog.setConfirmTxt("确定");
        dialog.hideOneButton();
        dialog.show();
    }

    public static void showSimpleErrorDialog(final Activity activity, String message) {
        BasicDialog dialog = new BasicDialog(activity, new BasicDialog.OnDialogListener() {
            @Override
            public void OnConfirm(BasicDialog dialog) {
                dialog.dismiss();
                activity.finish();
            }

            @Override
            public void OnCancel(BasicDialog dialog) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.setMainTxt(message);
        dialog.hideMinorTxt();
        dialog.setConfirmTxt("确定");
        dialog.hideOneButton();
        dialog.show();
    }

    public static Dialog showProgressDialog(Activity activity, String message) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setCancelable(false);
        dialog.setMessage(message);
        dialog.show();
        return dialog;
    }
}
