package com.brotherhood.o2o.controller;

import com.brotherhood.o2o.ui.widget.SwLin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by laimo.li on 2016/1/7.
 */
public class SwLinController {

    private Map<Integer, SwLin> mapView;
    private boolean mTouch = false;
    private String showMenuTag;

    public SwLinController() {
        mapView = new HashMap<Integer, SwLin>();
    }

    public void showMainLayout() {
        for (int key : mapView.keySet()) {
            mapView.get(key).showScreen(0);
        }
    }

    public void put(int position, SwLin swLin) {
        mapView.put(position, swLin);
        swLin.setTag(position);
        swLin.setScreenListener(new SwLin.ScreenListener() {
            @Override
            public boolean startTouch(String tag) {
                if (mTouch) {
                    if (showMenuTag.equals(tag)) {
                        mTouch = false;
                    } else {
                        int p = Integer.parseInt(showMenuTag);
                        showMainLayout();
                    }

                }
                return mTouch;
            }

            @Override
            public void canTouch(boolean flag) {
                mTouch = false;
            }

            @Override
            public void changeScreen(int screen, String tag) {
                if (screen == 1) {
                    mTouch = true;
                    showMenuTag = tag;
                }
            }
        });
    }


    public boolean isShowMenu(int position) {
        SwLin swLin = mapView.get(position);
        return swLin.isShowMenu();
    }

}
