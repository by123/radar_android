package com.brotherhood.o2o.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.R;

/**
 * Created by laimo.li on 2016/1/7.
 */
public class StateViewManager {


    public static View getEmpty(Context context, int textId, int resId) {
        return getEmpty(context, context.getString(textId), resId);
    }

    public static View getEmpty(Context context, String text, int resId) {
        View view = LayoutInflater.from(context).inflate(R.layout.data_empty_view, null);
        TextView textView = (TextView) view.findViewById(R.id.tvMessage);
        textView.setText(text);
        ImageView icon = (ImageView) view.findViewById(R.id.ivIcon);
        icon.setImageResource(resId);
        return view;
    }


}
