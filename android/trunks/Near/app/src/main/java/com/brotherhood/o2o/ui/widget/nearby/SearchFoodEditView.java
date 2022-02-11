package com.brotherhood.o2o.ui.widget.nearby;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;

/**
 * 搜索美食头部
 */
public class SearchFoodEditView extends LinearLayout implements View.OnClickListener {

    private CallBack mCallBack;

    private EditText mEtSearchFood;

    private ImageButton mIbEmptyKey;

    public SearchFoodEditView(final Context context) {
        this(context, null);
    }

    public SearchFoodEditView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setupViews(context);
    }

    private void setupViews(Context context) {
        final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.search_food_edit_view, this);
        findViewById(R.id.abBack).setOnClickListener(this);
        mIbEmptyKey = (ImageButton) findViewById(R.id.ibFoodEmptyKey);
        mIbEmptyKey.setOnClickListener(this);
        findViewById(R.id.btnSearchFood).setOnClickListener(this);

        mEtSearchFood = (EditText) findViewById(R.id.etSearchFood);
        mEtSearchFood.addTextChangedListener(textWatcher);
        mEtSearchFood.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (mCallBack != null) {
                        String keyWord = mEtSearchFood.getText().toString();
                        if (TextUtils.isEmpty(keyWord)) {
                            Toast.makeText(getContext(), getContext().getString(R.string.search_word_empty), Toast.LENGTH_SHORT).show();
                        } else {
                            mCallBack.search(keyWord);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void setEditText(String keyWord) {
        if (TextUtils.isEmpty(keyWord)) {
            return;
        }
        mEtSearchFood.setText(keyWord);
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable editable) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            showEditTextEmpty(s.length());
        }


    };


    private void showEditTextEmpty(int length) {
        if (length > 0) {
            mIbEmptyKey.setVisibility(View.VISIBLE);
        } else {
            mIbEmptyKey.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.abBack:
                if (mCallBack != null) {
                    mCallBack.back();
                }
                break;
            case R.id.btnSearchFood:
                if (mCallBack != null) {
                    String keyWord = mEtSearchFood.getText().toString();
                    if (TextUtils.isEmpty(keyWord)) {
                        Toast.makeText(getContext(), getContext().getString(R.string.search_word_empty), Toast.LENGTH_SHORT).show();
                    } else {
                        mCallBack.search(keyWord);
                    }
                }
                break;
            case R.id.ibFoodEmptyKey:
                mEtSearchFood.setText("");
                if (mCallBack != null) {
                    mCallBack.editTextFocused();
                }
                break;
            case R.id.etSearchFood://显示搜索记录
                if (mCallBack != null) {
                    mCallBack.editTextFocused();
                }
                break;
        }
    }

    public void setCallBack(CallBack callBack) {
        this.mCallBack = callBack;
    }


    public interface CallBack {

        void back();

        void search(String keyWord);

        void editTextFocused();

    }
}
