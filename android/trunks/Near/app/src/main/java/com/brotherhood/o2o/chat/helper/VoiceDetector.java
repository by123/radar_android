package com.brotherhood.o2o.chat.helper;

import android.media.MediaPlayer;

import com.skynet.library.message.Logger;


/**
 * Created by Administrator on 2015/12/21 0021.
 */
public class VoiceDetector {

    private MediaPlayer mDurationDetector;

    public long detectDuration(String filePath) {
        synchronized (this) {
            // With the synchronized keyword, we can avoid multi-thread occasion
            // which may cause MediaPlayer instances into wrong state
            long duration = -1L;
            try {
                if (mDurationDetector == null) {
                    mDurationDetector = new MediaPlayer();
                }
                mDurationDetector.reset();
                mDurationDetector.setDataSource(filePath);
                mDurationDetector.prepare();
                int millis = mDurationDetector.getDuration();
                if (millis > 0) {
                    int secs = millis / 1000;
                    if (secs == 0L) {
                        duration = 1L;
                    } else {
                        duration = secs + 1;
                    }
                }
            } catch (Exception e) {
                if (Logger.DEBUG) {
                    e.printStackTrace();
                }
            }
            return duration;
        }
    }

    /**
     * Why we release {@link #mDurationDetector} here instead of in the
     * {@link #close()} method, because {@link mDurationDetector} maybe still in
     * use in a future time, remember there maybe a voice downloading process at
     * that time.
     */
    public void finalize() {
        if (mDurationDetector != null) {
            try {
                mDurationDetector.release();
            } catch (Exception e) {
            }
        }
    }
}
