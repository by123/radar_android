package com.brotherhood.o2o.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;

/**
 * Created by billy.shi on 2016/1/11.
 */
public class DialogUtil {


    public static Dialog createLoadingDialog(Context context) {
        return createLoadingDialog(context, context.getString(R.string.loading));
    }

    /**
     * @param context 上下文
     * @param msg     加载的文字
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局

        TextView tipTextView = (TextView) v.findViewById(R.id.tvLoading);// 提示文字
        tipTextView.setText(msg);//设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;

    }


}
