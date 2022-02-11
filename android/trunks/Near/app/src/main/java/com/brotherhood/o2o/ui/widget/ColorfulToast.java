package com.brotherhood.o2o.ui.widget;

import android.content.Context;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.ui.widget.dialog.ToastDialog;

/**
 * Created by ZhengYi on 15/6/11.
 */
public class ColorfulToast {

    static ToastDialog toastDialog;

    public static final String COLOR_ORANGE = "#ea5f2d";
    public static final String COLOR_BLUE = "#3d97e9";
    public static final String COLOR_GREEN = "#65a92e";
    public static final String COLOR_RED = "#ff5454";

    public static void orange(Context context, String msg, int duration) {
        show(context, COLOR_ORANGE, R.mipmap.toast_ic_error, msg, duration);
    }

    public static void green(Context context, String msg, int duration) {
        show(context, COLOR_GREEN, R.mipmap.ic_login_correct_normal, msg, duration);
    }

    /*public static void blue(Context context, String msg, int duration) {
        show(context, COLOR_BLUE, R.mipmap.ic_login_correct_normal, msg, duration);
    }*/

    public static void green_no_icon(Context context, String msg, int duration) {
        show(context, COLOR_GREEN, -1, msg, duration);
    }

    public static void orangeNoIcon(Context context, String msg, int duration) {
        show(context, COLOR_ORANGE, -1, msg, duration);
    }

    public static void redNoIcon(Context context, String msg, int duration) {
        show(context, COLOR_RED, -1, msg, duration);
    }

    //private static void show(Context context, String color, int iconResId, String msg, int duration) {
    //
    //    Toast toast = new Toast(context);
    //    View v = View.inflate(context, R.layout.widget_colorful_toast, null);
    //    View rootContainer = v.findViewById(R.id.container_root);
    //    rootContainer.getLayoutParams().width = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    //    rootContainer.setBackgroundColor(Color.parseColor(color));
    //    TextView contentLabel = (TextView) v.findViewById(R.id.label_content);
    //    contentLabel.setText(msg);
    //    ImageView iconImage = (ImageView) v.findViewById(R.id.image_icon);
    //    if (iconResId != -1) {
    //        iconImage.setImageResource(iconResId);
    //    } else {
    //        iconImage.setVisibility(View.GONE);
    //    }
    //    toast.setGravity(Gravity.BOTTOM, 0, 0);
    //    toast.setView(v);
    //    toast.setDuration(duration);
    //    toast.show();
    //}


    private static void show(Context context, String color, int iconResId, String msg, int duration) {
        if (toastDialog == null) {
            toastDialog = new ToastDialog(NearApplication.mInstance.getApplicationContext());
        }
        toastDialog.show(color, iconResId, msg,duration);
    }


}
