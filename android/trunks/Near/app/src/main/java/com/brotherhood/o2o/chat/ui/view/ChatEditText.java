package com.brotherhood.o2o.chat.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

public class ChatEditText extends EditText {

	private static final boolean DEBUG = false;

	private static final String TAG = "ChatEditText";

	public ChatEditText(Context context) {
		super(context);
	}

	public ChatEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ChatEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@SuppressLint("NewApi")
	public ChatEditText(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		boolean superHandled = super.onKeyPreIme(keyCode, event);
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_UP) {
			if (mListener != null) {
				return mListener.onImeBack();
			}
		}
		return superHandled;
	}

	private ChatEditTextListener mListener;

	public void setChatEditTextListener(ChatEditTextListener l) {
		mListener = l;
	}

	public static interface ChatEditTextListener {

		/**
		 * If you want this event to be consumed here by yourself, return true
		 * 
		 * @return
		 */
		public boolean onImeBack();
	}

	public void addEmotion(int resId, String desc) {
		Editable editable = getText();
		int startIdx = Selection.getSelectionStart(editable);
		int endIdx = Selection.getSelectionEnd(editable);
		if (DEBUG) {
			Log.i(TAG, "addEmotion start=" + startIdx + ", end=" + endIdx);
		}

		// Copy the original text and make it mutable
		SpannableStringBuilder copy = new SpannableStringBuilder(editable);
		copy.replace(startIdx, endIdx, desc);
		copy.setSpan(
				new ImageSpan(getContext(), resId, ImageSpan.ALIGN_BOTTOM),
				startIdx, startIdx + desc.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		setText(copy);

		final int cursorPos = startIdx + desc.length();
		post(new Runnable() {

			@Override
			public void run() {
				Selection.setSelection(getText(), cursorPos);
			}
		});
	}

	public void backwardDelete() {
		Editable editable = getText();
		int startIdx = Selection.getSelectionStart(editable);
		int endIdx = Selection.getSelectionEnd(editable);
		if (DEBUG) {
			Log.i(TAG, "backwardDelete, start=" + startIdx + ", end=" + endIdx);
		}
		if (startIdx == 0 && endIdx == 0) {
			if (DEBUG) {
				Log.i(TAG, "cursor already at the far left, ignore");
			}
			return;
		}

		// Copy the original text and make it mutable
		int cursorPos = 0;
		SpannableStringBuilder copy = new SpannableStringBuilder(editable);
		do {
			if (startIdx != endIdx) {
				// delete the selected text
				if (DEBUG) {
					Log.i(TAG, "has selected text, delete...");
				}
				copy.replace(startIdx, endIdx, "");
				cursorPos = startIdx;
				break;
			}
			int queryEnd = startIdx;
			int queryStart = startIdx - 1;
			if (DEBUG) {
				Log.i(TAG, "getSpans, start=" + queryStart + ", end="
						+ queryEnd);
			}
			Object[] objs = copy.getSpans(queryStart, queryEnd, Object.class);
			if (objs == null || objs.length <= 0) {
				if (DEBUG) {
					Log.i(TAG, "probably just a character left of cursor");
				}
				// No Span object found, just a normal character left of cursor
				copy.replace(startIdx - 1, startIdx, "");
				cursorPos = startIdx - 1;
				break;
			}

			if (DEBUG) {
				Log.i(TAG, "find " + objs.length + " objects");
			}
			// should be only one
			Object target = objs[0];
			if (DEBUG) {
				Log.i(TAG, "find " + target.getClass().getSimpleName());
			}
			int posStart = copy.getSpanStart(target);
			int posEnd = copy.getSpanEnd(target);
			// The Object is right left of the cursor, so...delete it
			copy.replace(posStart, posEnd, "");
			cursorPos = posStart;
		} while (false);

		setCursorVisible(false);
		setText(copy);
		final int finalPos = cursorPos;
		post(new Runnable() {

			@Override
			public void run() {
				Selection.setSelection(getText(), finalPos);
				setCursorVisible(true);
			}
		});
	}

}
