package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.adapter.PhotosAdapter;
import com.brotherhood.o2o.ui.widget.account.PhotoDecoration;
import com.brotherhood.o2o.util.DisplayUtil;

import java.util.ArrayList;

/**
 * Created by laimo.li on 2016/1/22.
 */
public class PhotosActivity extends BaseActivity {

    public static final String KEY_PHOTOS = "key_photos";

    private static final int NUMBER_COLUMNS = 3;
    private static final int mItemSpace = DisplayUtil.dp2px(3);

    @ViewInject(id = R.id.rvBaseRecycler)
    private RecyclerView mRecyclerView;

    private ArrayList<String> photos;

    private PhotosAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photos_layout;
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

        photos = getIntent().getStringArrayListExtra(KEY_PHOTOS);

        getActionBarController().setBaseTitle(photos.size() + " PHOTOS", R.color.slide_menu_holo_black).setHeadBackgroundColor(R.color.white);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, NUMBER_COLUMNS));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new PhotoDecoration(NUMBER_COLUMNS, mItemSpace, false));

        adapter = new PhotosAdapter(this, photos);
        mRecyclerView.setAdapter(adapter);


    }


    public static void show(Context context, ArrayList<String> photos) {
        Intent intent = new Intent(context, PhotosActivity.class);
        intent.putStringArrayListExtra(KEY_PHOTOS, photos);
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
