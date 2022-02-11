package com.brotherhood.o2o.personal.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by by.huang on 2015/6/9.
 */
public class SystemMsgDetailActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.txt_title)
    TextView mTitleTxt;

    @InjectView(R.id.txt_content)
    TextView mContentTxt;

    private static final String EXTRA_TITLE = "title";
    private static final String EXTAR_CONTENT = "content";


    public static void show(Context context, String title, String content) {
        Intent intent = new Intent(context, SystemMsgDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTAR_CONTENT, content);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_systemmsg_detail);
        ButterKnife.inject(this);
        initData();
    }

    private void initData() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String content = getIntent().getStringExtra(EXTAR_CONTENT);
        mTitleTxt.setText(title);
        mContentTxt.setText(content);
    }

}
