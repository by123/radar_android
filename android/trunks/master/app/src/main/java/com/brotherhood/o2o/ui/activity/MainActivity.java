package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.component.AccountComponent;
import com.brotherhood.o2o.explore.controller.ExploreFragment;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.fragment.SlideMenuFragment;
import com.brotherhood.o2o.config.Constants;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends BaseActivity {

    @InjectView(R.id.container_drawer)
    DrawerLayout mDrawerLayout;

    public static void show(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        ButterKnife.inject(this);
        getSupportFragmentManager().beginTransaction().add(R.id.container, new ExploreFragment()).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.drawer_left, new SlideMenuFragment()).commit();
        AccountComponent.shareComponent().autoLogin(this);
    }

    @Override
    protected boolean containFragment() {
        return true;
    }

    public void toggleSlideMenu() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.IS_UPDATED) {
            Constants.IS_UPDATED = false;
            AccountComponent.shareComponent().getUserInfo();
        }
    }
}
