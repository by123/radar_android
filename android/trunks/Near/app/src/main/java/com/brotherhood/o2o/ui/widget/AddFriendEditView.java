package com.brotherhood.o2o.ui.widget;

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

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.util.DisplayUtil;
import com.brotherhood.o2o.util.Utils;


public class AddFriendEditView extends LinearLayout implements View.OnClickListener {

    private CallBack callBack;

    private LinearLayout lySearch;

    private EditText etAddFriend;

    private ImageButton ibEmptyKey;

    public AddFriendEditView(final Context context) {
        this(context, null);
    }

    public AddFriendEditView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setupViews(context);
    }

    private void setupViews(Context context) {
        final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.add_friend_edit_view, this);
        findViewById(R.id.abBack).setOnClickListener(this);

        lySearch = (LinearLayout) findViewById(R.id.lySearch);
        lySearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etAddFriend.setFocusable(true);
                DisplayUtil.showKeyboard(getContext());
            }
        });

        ibEmptyKey = (ImageButton) findViewById(R.id.ibEmptyKey);
        ibEmptyKey.setOnClickListener(this);
        findViewById(R.id.btnSearch).setOnClickListener(this);

        etAddFriend = (EditText) findViewById(R.id.etAddFriend);
        etAddFriend.addTextChangedListener(textWatcher);
        etAddFriend.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });


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
            ibEmptyKey.setVisibility(View.VISIBLE);
        } else {
            ibEmptyKey.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.abBack:
                if (callBack != null) {
                    callBack.back();
                }
                break;
            case R.id.btnSearch:
                search();
                break;
            case R.id.ibEmptyKey:
                etAddFriend.setText("");
                break;
        }
    }

    private void search() {
        if (callBack != null) {
            String nickname = etAddFriend.getText().toString();
            if (TextUtils.isEmpty(nickname.replace(" ", ""))) {
                ColorfulToast.orange(getContext(), getContext().getString(R.string.put_friend_name_hint), 0);
                return;
            }
            if (Utils.containsEmoji(nickname)) {
                ColorfulToast.orange(getContext(), getContext().getString(R.string.friend_name_not_allow_emoji), 0);
                return;
            }
            callBack.search(nickname);
        }
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }


    public interface CallBack {

        void back();

        void search(String nickname);

    }
}
