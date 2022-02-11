package com.brotherhood.o2o.chat.helper;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.skynet.library.message.Logger;
import com.skynet.library.message.MessageManager;

import java.io.File;

/**
 * Created by Administrator on 2015/12/19 0019.
 */
public class VoiceRecorder {
    private static final String TAG = "VOICE_RECORDER";
    private MediaRecorder mVoiceRecorder;
    private long mStartRecordingTime;
    private String mVoiceFileName;
    private Context mContext;
    public final static int MAX_TIME = 60 * 1000;
    private boolean mStartRecord = false;

    public VoiceRecorder(Context context) {
        mContext = context;
    }

    public void startRecordingVoice() {
        mStartRecord = true;
        MediaRecorder cache = mVoiceRecorder;
        if (cache == null) {
            cache = new MediaRecorder();
            mVoiceRecorder = cache;
        }
        mStartRecordingTime = SystemClock.uptimeMillis();
        try {
            cache.setAudioSource(MediaRecorder.AudioSource.MIC);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1) {
                cache.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            } else {
                cache.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            }
            cache.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            cache.setAudioEncodingBitRate(5600);
            cache.setMaxDuration(MAX_TIME);
            mVoiceFileName = "out_" + System.currentTimeMillis() + ".amr";
            cache.setOutputFile(IDSIMManager.getInstance().getVoicePath() + "/" + mVoiceFileName);
            cache.prepare();
            cache.start();
        } catch (Exception e) {
            e.printStackTrace();
            mStartRecord = false;
        }
    }

    public void sendRecordedVoice(ChatSenderHelper helper) {
        if (!mStartRecord) {
            return;
        }
        long interval = SystemClock.uptimeMillis() - mStartRecordingTime;
        if (interval < 1000) {
            Toast.makeText(mContext,
                    R.string.tips_recorded_voice_too_short, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (interval > MAX_TIME) {
            interval = MAX_TIME;
        }
        long seconds = interval / 1000L;
        if (Logger.DEBUG) {
            Logger.d(TAG, "record interval=" + interval + ", seconds="
                    + seconds);
        }

        File f = new File(IDSIMManager.getInstance().getVoicePath(), mVoiceFileName);
        byte[] content = Utils.readFileToBytes(f);
        if (content == null) {
            Toast.makeText(mContext,
                    R.string.chat_record_audio_error_no_sdcard,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.length <= 6) {
            Toast.makeText(mContext,
                    R.string.chat_record_audio_error_no_permission,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        helper.sendVoice(f.getPath(), seconds);
        mStartRecord = false;
    }

    public void stopRecordingVoice() {
        if (mVoiceRecorder == null) {
            return;
        }
        try {
            mVoiceRecorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mVoiceRecorder.reset();
        mVoiceRecorder.release();
        mVoiceRecorder = null;
    }


    public void release() {
        if (mVoiceRecorder != null) {
            try {
                mVoiceRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
