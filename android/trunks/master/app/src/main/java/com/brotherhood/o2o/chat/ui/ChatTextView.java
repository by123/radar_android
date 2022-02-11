package com.brotherhood.o2o.chat.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.brotherhood.o2o.chat.utils.SkipProguardInterface;
import com.skynet.library.message.Logger;

public class ChatTextView extends TextView implements SkipProguardInterface {

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
		Pattern p = Pattern.compile("\\[[^]\\[]+?]");
		Matcher matcher = p.matcher(text);
		while (matcher.find()) {
			String occurrence = matcher.group();
			if (DEBUG) {
				Log.d(TAG, "found match: " + occurrence);
			}
			if (occurrence.equals("[DEL]")) {
				if (DEBUG) {
					Log.d(TAG, "[DEL], continue");
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
