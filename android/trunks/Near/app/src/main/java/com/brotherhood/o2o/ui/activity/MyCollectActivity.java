package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;

/**
 * Created by laimo.li on 2016/1/6.
 */
public class MyCollectActivity extends BaseActivity {

    //@ViewInject(id = R.id.vpMyCollect)
    //private ViewPager mViewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_collect_layout;
    }

    @Override
    protected boolean addActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBarController().setBaseTitle(R.string.my_collect, R.color.slide_menu_holo_black).setHeadBackgroundColor(R.color.white);

        //FragmentManager fragmentManager = getSupportFragmentManager();
        //mViewPager.setAdapter(new MyCollectListAdapter(fragmentManager));
        //mViewPager.setOnTouchListener(new View.OnTouchListener(){
        //
        //    @Override
        //    public boolean onTouch(View v, MotionEvent event) {
        //        return true;
        //    }
        //});
    }




    //public static class MyCollectListAdapter extends FragmentStatePagerAdapter {
    //
    //    public MyCollectListAdapter(FragmentManager fm) {
    //        super(fm);
    //    }
    //
    //    @Override
    //    public Fragment getItem(int position) {
    //        Fragment Fragment = MyCollectListFragment.newInstance();
    //        return Fragment;
    //    }
    //
    //    @Override
    //    public int getCount() {
    //        return 2;
    //    }
    //
    //    @Override
    //    public Object instantiateItem(ViewGroup arg0, int arg1) {
    //        return super.instantiateItem(arg0, arg1);
    //    }
    //
    //    @Override
    //    public void destroyItem(ViewGroup container, int position, Object object) {
    //        super.destroyItem(container, position, object);
    //    }
    //
    //}


    public static void show(Context context){
        Intent intent = new Intent(context,MyCollectActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
        }
    }

}
