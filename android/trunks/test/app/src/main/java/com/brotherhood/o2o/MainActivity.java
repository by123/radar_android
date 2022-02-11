package com.brotherhood.o2o;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.brotherhood.o2o.address.AddressComponent;
import com.brotherhood.o2o.address.helper.LocationHelper;
import com.brotherhood.o2o.address.model.AddressInfo;
import com.brotherhood.o2o.category.controller.CategoryFragment;
import com.brotherhood.o2o.database.PreferenceHelper;
import com.brotherhood.o2o.explore.controller.ExploreFragment;
import com.brotherhood.o2o.extensions.BaseActivity;
import com.brotherhood.o2o.personal.controller.AboutFragment;
import com.brotherhood.o2o.personal.controller.AdviceFragment;
import com.brotherhood.o2o.personal.controller.PersonalFragment;
import com.brotherhood.o2o.personal.controller.SystemMsgFragment;
import com.brotherhood.o2o.personal.controller.UserInfoFragment;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.Constants;
import com.brotherhood.o2o.utils.Utils;
import com.brotherhood.o2o.widget.BottomBar;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends BaseActivity {

    public ExploreFragment mExploreFragment;
    public PersonalFragment mPersonalFragment;
    public CategoryFragment mCategoryFragment;
    public static MainActivity mActivity;
    //传感器
    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private float[] mValues = {180, 0, 0};

    private Fragment mCurrentFragment;

    public enum SystemMsg {List, Detail}

    ;
    public SystemMsg msg = SystemMsg.List;
    public boolean readAll = false;

    @InjectView(R.id.bottombar)
    View mBottomBar;

    public static void show(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        ButterKnife.inject(this);
        mActivity = this;
        setScreen();
        setSensor();
        setBottomBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSensorManager != null && mSensorListener != null) {
            mSensorManager.registerListener(mSensorListener, mOrientation,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mSensorManager != null && mSensorListener != null) {
            mSensorManager.unregisterListener(mSensorListener);
        }
    }

    @Override
    protected boolean containFragment() {
        return true;
    }

    private SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] mTempValues = event.values;
            mValues[0] = (int) mTempValues[0];
            mValues[1] = (int) mTempValues[1];
            mValues[2] = (int) mTempValues[2];
            if (mExploreFragment == null || mExploreFragment.getRadarContentView() == null)
                return;
            ViewHelper.setRotation(mExploreFragment.getRadarContentView(), -mValues[0]);
            ViewHelper.setRotation(mExploreFragment.getRadarCenterView(), mValues[0]);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private void setBottomBar() {
        int position = PreferenceHelper.sharePreference(this).getInt(Constants.PREFER_BOTTOMBAR, 0);
        selectFragment(position);
        BottomBar.addBottomBar(this, new BottomBar.OnSelectItemListener() {
            @Override
            public void OnSelectedItem(int position) {
                selectFragment(position);
            }
        });
    }

    private void selectFragment(int position) {
        switch (position) {
            case 0:
                mCategoryFragment = new CategoryFragment();
                swichToFragment(mCategoryFragment);
                break;
            case 1:
                mExploreFragment = new ExploreFragment();
                swichToFragment(mExploreFragment);
                break;
            case 2:
                mPersonalFragment = new PersonalFragment();
                swichToFragment(mPersonalFragment);
                break;
        }
    }

    private void setSensor() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(mSensorListener, mOrientation,
                SensorManager.SENSOR_DELAY_GAME);
    }

    private void setScreen() {
        Constants.ScreenWidth = Utils.getScreenWidth(this);
        Constants.ScreenHeight = Utils.getScreentHeight(this);
    }

    public void swichToFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        mCurrentFragment = fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void swichToFragment(Fragment fragment, boolean left) {
        if (fragment == null) {
            return;
        }
        mCurrentFragment = fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        if (left) {
//            transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out, R.anim.push_right_out, R.anim.push_left_in);
//        } else {
//            transaction.setCustomAnimations(R.anim.push_right_out, R.anim.push_left_in, R.anim.push_right_in, R.anim.push_left_out);
//        }
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCurrentFragment instanceof AdviceFragment || mCurrentFragment instanceof AboutFragment || mCurrentFragment instanceof UserInfoFragment) {
                swichToFragment(new PersonalFragment(), false);
                return true;
            }
            if (mCurrentFragment instanceof SystemMsgFragment && msg == SystemMsg.Detail) {
                ((SystemMsgFragment) mCurrentFragment).changeList();
                return true;
            }
            if (mCurrentFragment instanceof SystemMsgFragment && msg == SystemMsg.List) {
                swichToFragment(new PersonalFragment(), false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void isNeedBottomBar(boolean need) {
        if (need) {
            mBottomBar.setVisibility(View.VISIBLE);
        } else {
            mBottomBar.setVisibility(View.GONE);
        }
    }


}
