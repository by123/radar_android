package com.brotherhood.o2o.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.utils.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by ZhengYi on 15/6/8.
 */
public class SearchBar extends FrameLayout {

    private static final int DECORATION_WHITE = 0;

    private EditText mKeywordField;
    private Button mSearchButton;
    private WeakReference<SearchBarDelegate> mDelegateRefOrNil;

    public SearchBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.widget_search_bar, this, true);
        bindSubViews();
        initView(attrs);
    }

    public void setDelegate(SearchBarDelegate delegateOrNil) {
        if (delegateOrNil != null)
            mDelegateRefOrNil = new WeakReference<SearchBarDelegate>(delegateOrNil);
    }

    public void releaseFocus() {
        Utils.hideKeyboard(mKeywordField);
    }

    public void setKeyword(String keyword) {
        mKeywordField.setText(keyword);
    }

    public String getKeyword() {
        return mKeywordField.getText().toString().trim();
    }

    private void bindSubViews() {
        mKeywordField = (EditText) findViewById(R.id.widget_field_keyword);
        mSearchButton = (Button) findViewById(R.id.widget_btn_search);
    }

    private void initView(AttributeSet attrsOrNil) {
        if (attrsOrNil != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrsOrNil, R.styleable.SearchBar);
            String hintOrNil = typedArray.getString(R.styleable.SearchBar_hint);
            if (TextUtils.isEmpty(hintOrNil))
                mKeywordField.setHint("");
            else
                mKeywordField.setHint(hintOrNil);

            //默认采用白色主题
            if (true) {
                setBackgroundColor(getResources().getColor(android.R.color.white));
                mSearchButton.setTextColor(Color.parseColor("#333333"));
                mKeywordField.setTextColor(Color.parseColor("#333333"));
                mKeywordField.setHintTextColor(Color.parseColor("#808080"));
            }

            typedArray.recycle();
        }

        mKeywordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    releaseFocus();
                    String keyword = mKeywordField.getText().toString().trim();
                    if (!TextUtils.isEmpty(keyword)) {
                        getDelegate().onSearchButtonClick(SearchBar.this, keyword);
                    }
                }
                return true;
            }
        });
        mKeywordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getDelegate().onTextChanged(SearchBar.this, s.toString().trim());
            }
        });

        mSearchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseFocus();
                String keyword = mKeywordField.getText().toString().trim();
                if (!TextUtils.isEmpty(keyword)) {
                    getDelegate().onSearchButtonClick(SearchBar.this, keyword);
                }
            }
        });
    }

    private SearchBarDelegate getDelegate() {
        if (mDelegateRefOrNil != null && mDelegateRefOrNil.get() != null) {
            return mDelegateRefOrNil.get();
        }
        return SearchBarDelegate.NULL;
    }

    public interface SearchBarDelegate {
        void onTextChanged(SearchBar searchBar, String text);

        void onSearchButtonClick(SearchBar searchBar, String keyword);

        SearchBarDelegate NULL = new SearchBarDelegate() {
            @Override
            public void onSearchButtonClick(SearchBar searchBar, String keyword) {
            }

            @Override
            public void onTextChanged(SearchBar searchBar, String text) {
            }
        };
    }
}
