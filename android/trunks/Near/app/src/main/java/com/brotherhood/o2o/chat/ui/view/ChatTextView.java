package com.brotherhood.o2o.chat.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.brotherhood.o2o.chat.ui.EmotionManager;
import com.skynet.library.message.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatTextView extends TextView {

	private static final boolean DEBUG = Logger.DEBUG;

	private static final String TAG = "ChatTextView";

	@SuppressLint("NewApi")
	public ChatTextView(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public ChatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ChatTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ChatTextView(Context context) {
		super(context);
	}

	public void resolveText(String text) {
		if (DEBUG) {
			Log.d(TAG, "resolveText text: " + text);
		}
		SpannableStringBuilder copy = new SpannableStringBuilder(text);
//		Pattern p = Pattern.compile("\\[[^]\\[]+?]");
		Pattern p = Pattern.compile("(:[a-z0-9-+_]+:)");
		Matcher matcher = p.matcher(text);
		while (matcher.find()) {
			String occurrence = matcher.group();
			if (DEBUG) {
				Log.d(TAG, "found match: " + occurrence);
			}
			if (occurrence.equals(EmotionManager.DEL_DESC)) {
				if (DEBUG) {
					Log.d(TAG, "[DEL_DESC], continue");
				}
				continue;
			}
			int id = EmotionManager.getEmotionResourceId(occurrence);
			if (id != -1) {
				ImageSpan imageSpan = (new ImageSpan(getContext(), id,
						ImageSpan.ALIGN_BOTTOM));
				int pos = matcher.end();
				copy.setSpan(imageSpan, pos - occurrence.length(), pos,
						SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		setText(copy);
	}
}
