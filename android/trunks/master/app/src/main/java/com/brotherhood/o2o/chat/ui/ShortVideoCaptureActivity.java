package com.brotherhood.o2o.chat.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.utils.SkipProguardInterface;
import com.github.snowdream.android.util.Log;


public class ShortVideoCaptureActivity extends ActionBarActivity implements
		SurfaceHolder.Callback, View.OnTouchListener, SkipProguardInterface,
		TimeIndicatorView.EventListener, View.OnClickListener {

	private static final String TAG = "ShortVideoCaptureActivity";
	public static final boolean DEBUG = true;

	private TextView mController;
	private SurfaceView mSurfaceView;
	private MediaPlayer mPlayer;

	private boolean mHasInitialized;
	private boolean mIsPreviewing;
	private Point mCameraResolution;
	private Camera mCamera;
	private MediaRecorder mMediaRecorder;
	private String mVideoFilePath;
	private TimeIndicatorView mTimeIndicatorView;

	private boolean mIsRecording;

	private TextView mBtnCancel;
	private TextView mBtnSend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_short_video_capture_layout);
		mController = (TextView) findViewById(R.id.chat_short_video_capture_controller);
		mController.setOnTouchListener(this);
		mBtnSend = (TextView) findViewById(R.id.chat_short_video_capture_send);
		mBtnSend.setOnClickListener(this);
		mBtnCancel = (TextView) findViewById(R.id.chat_short_video_capture_cancel);
		mBtnCancel.setOnClickListener(this);
		mSurfaceView = (SurfaceView) findViewById(R.id.chat_short_video_camera_surface);
		mTimeIndicatorView = (TimeIndicatorView) findViewById(R.id.chat_short_video_time_indicator);
		mTimeIndicatorView.setEventListener(this);
		mTimeIndicatorView.setIndicator(new ColorDrawable(Color.GREEN));
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(null);
		actionBar.setDisplayHomeAsUpEnabled(true);

		prepareToCapture();
	}

	private void prepareToCapture() {
		mBtnSend.setVisibility(View.GONE);
		mBtnCancel.setVisibility(View.GONE);

		mController.setVisibility(View.VISIBLE);
	}

	private void prepareToSend() {
		mBtnSend.setVisibility(View.VISIBLE);
		mBtnCancel.setVisibility(View.VISIBLE);

		mController.setVisibility(View.GONE);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private boolean mCaptureEnded;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mController.setSelected(true);
			mController.setText(R.string.tips_untouch_to_cancel);
			mCaptureEnded = false;
			if (prepareVideoRecorder()) {
				mMediaRecorder.start();
				mTimeIndicatorView.start();
				mIsRecording = true;
			} else {
				// prepare didn't work, release the camera
				releaseMediaRecorder();
				// inform user
				Toast.makeText(this, R.string.chat_failed_to_capture_video,
						Toast.LENGTH_SHORT).show();
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mController.setText(R.string.tips_press_to_capture_video);
			mController.setSelected(false);
			if (!mCaptureEnded) {
				// Well the
				// stop recording and release camera
				try {
					mMediaRecorder.stop(); // stop the recording
				} catch (Exception e) {
					// Stop failed, WTF
				}
				releaseMediaRecorder(); // release the MediaRecorder object
				mCamera.lock(); // take camera access back from
								// MediaRecorder
				// inform the user that recording has stopped
				mIsRecording = false;
				mTimeIndicatorView.cancel();
				if (mVideoFilePath != null) {
					File targetFile = new File(mVideoFilePath);
					targetFile.delete();
				}
			}
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnSend) {
			Intent intent = new Intent();
			intent.putExtra("video_uri", mVideoFilePath);
			setResult(RESULT_OK, intent);
			finish();
		} else if (v == mBtnCancel) {
			if (mPlayer != null) {
				mPlayer.stop();
				mPlayer.release();
			}
			mTimeIndicatorView.cancel();
			prepareToCapture();
			try {
				mCamera.setPreviewDisplay(mSurfaceView.getHolder());
				mCamera.startPreview();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onEnd() {
		prepareToSend();
		mCaptureEnded = true;
		mController.setVisibility(View.GONE);
		// stop recording and release camera
		try {
			mMediaRecorder.stop(); // stop the recording
		} catch (Exception e) {
			// Stop failed, WTF
		}
		releaseMediaRecorder(); // release the MediaRecorder object
		mCamera.lock(); // take camera access back from MediaRecorder
		try {
			mCamera.stopPreview();
			mCamera.setPreviewDisplay(null);
		} catch (Exception e) {
		}

		// inform the user that recording has stopped
		mIsRecording = false;

		Toast.makeText(this, "录制完成", Toast.LENGTH_SHORT).show();

		playVideo();
	}

	private void playVideo() {
		if (mPlayer != null) {
			mPlayer.release();
		}
		MediaPlayer player = new MediaPlayer();
		mPlayer = player;
		try {
			player.setDataSource(mVideoFilePath);
			player.setLooping(true);
			player.setDisplay(mSurfaceView.getHolder());
			player.prepare();
			player.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	private boolean prepareVideoRecorder() {
		mMediaRecorder = new MediaRecorder();

		// Step 1: Unlock and set camera to MediaRecorder
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);

		// Step 2: Set sources
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		CamcorderProfile profile = CamcorderProfile
				.get(CamcorderProfile.QUALITY_LOW);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mMediaRecorder.setVideoFrameRate(24);
		mMediaRecorder.setVideoSize(profile.videoFrameWidth,
				profile.videoFrameHeight);
		mMediaRecorder.setVideoEncodingBitRate(4 * profile.videoBitRate);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		} else {
			mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
		}

		mMediaRecorder.setAudioEncodingBitRate(profile.audioBitRate);
		mMediaRecorder.setAudioChannels(profile.audioChannels);
		mMediaRecorder.setAudioSamplingRate(profile.audioSampleRate);
		mMediaRecorder.setAudioEncoder(profile.audioCodec);

		// Step 4: Set output file
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd-HH-mm-ss", Locale.US);
		String now = dateFormat.format(new Date());
		mVideoFilePath = new File(Environment.getExternalStorageDirectory(),
				now + ".mp4").getPath();

		// mVideoFilePath = ChatManager.getDefault(this).getVideoPath() +
		// "/VID_"
		// + System.currentTimeMillis() + ".mp4";
		mMediaRecorder.setOutputFile(mVideoFilePath);

		// Step 5: Set the preview output
		// No need to do this
		// mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());

		// Step 7: Set rotation of the recorded video
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mMediaRecorder.setOrientationHint(90);
		}

		// Step 8: Prepare configured MediaRecorder
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			Log.d(TAG,
					"IllegalStateException preparing MediaRecorder: "
							+ e.getMessage());
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		}
		return true;
	}

	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			mMediaRecorder.reset(); // clear recorder configuration
			mMediaRecorder.release(); // release the recorder object
			mMediaRecorder = null;
			mCamera.lock(); // lock camera for later use
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (DEBUG) {
			Log.e(TAG, "surfaceCreated " + holder.hashCode());
		}
		if (!checkCameraHardware()) {
			showErrorAndExit();
			return;
		}
		// Should not sleep on UI-thread, but here this is not a case
		int width = mSurfaceView.getWidth();
		int height = mSurfaceView.getHeight();
		while (width == 0 || height == 0) {
			// Wait until surface has a fixed size
			try {
				Thread.sleep(20);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (DEBUG) {
			Log.e(TAG, "camera view, w=" + width + ", h=" + height);
		}
		AsyncTask<Object, Void, Boolean> task = new AsyncTask<Object, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {
				if (DEBUG) {
					Log.e(TAG, "openDriver");
				}
				try {
					openDriver((SurfaceHolder) params[0], (Integer) params[1],
							(Integer) params[2]);
					if (DEBUG) {
						Log.e(TAG, "succeeded to openDriver");
					}
				} catch (Exception e) {
					e.printStackTrace();
					return Boolean.FALSE;
				}
				startPreview();
				return Boolean.TRUE;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (!result.booleanValue()) {
					showErrorAndExit();
				}
			}
		};
		task.execute(holder, width, height);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	private void showErrorAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setMessage(R.string.chat_open_camera_failed);
		builder.setPositiveButton(R.string.chat_exit,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		builder.show();
	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware() {
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (DEBUG) {
			Log.e(TAG, "surfaceDestroyed " + holder.hashCode());
		}
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				if (DEBUG) {
					Log.e(TAG, "closeDriver");
				}
				stopPreview();
				closeDriver();
				return null;
			}
		};
		task.execute();

	}

	/**
	 * Opens the camera driver and initializes the hardware parameters.
	 * 
	 * @param holder
	 *            The surface object which the camera will draw preview frames
	 *            into.
	 * @throws IOException
	 *             Indicates the camera driver failed to open.
	 */
	public void openDriver(SurfaceHolder holder, int width, int height)
			throws IOException {
		if (mCamera == null) {
			try {
				mCamera = Camera.open();
			} catch (Exception e) {
				e.printStackTrace();
				// Camera is not available(in use or does not exist)
			}
			if (mCamera == null) {
				throw new IOException();
			}
			mCamera.setPreviewDisplay(holder);
			if (!mHasInitialized) {
				mHasInitialized = true;
				initFromCameraParameters(mCamera, width, height);
			}
			setDesiredCameraParameters(mCamera);
		}
	}

	/**
	 * Reads, one time, values from the camera that are needed by the app.
	 */
	@SuppressLint("NewApi")
	void initFromCameraParameters(Camera camera, int width, int height) {
		Camera.Parameters param = camera.getParameters();
		Camera.Size size = getCameraResolutionBeforeAPI21(param, width, height);
		mCameraResolution = new Point(size.width, size.height);
		Log.d(TAG, "Camera resolution: " + mCameraResolution);
	}

	/**
	 * Sets the camera up to take preview images which are used for both preview
	 * and decoding. We detect the preview format here so that
	 * buildLuminanceSource() can build an appropriate LuminanceSource subclass.
	 * In the future we may want to force YUV420SP as it's the smallest, and the
	 * planar Y can be used for barcode scanning without a copy in some cases.
	 */
	@SuppressLint("NewApi")
	void setDesiredCameraParameters(Camera camera) {
		camera.setDisplayOrientation(90);
		Camera.Parameters param = camera.getParameters();
		param.setPreviewFormat(ImageFormat.NV21);
		Log.d(TAG, "Setting preview size: " + mCameraResolution);
		if (param.isVideoStabilizationSupported()) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
				param.setVideoStabilization(true);
		}
		param.setPreviewSize(mCameraResolution.x, mCameraResolution.y);
		param.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		param.setZoom(0);
		camera.setParameters(param);
	}

	private Camera.Size getCameraResolutionBeforeAPI21(Camera.Parameters param,
			int width, int height) {
		List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
		Camera.Size targetSize = null;
		int minDiff = Integer.MAX_VALUE;
		for (Camera.Size size : previewSizes) {
			// MODIFIED Since our application is portrait, so we need to
			// adapt
			// int newDiff = Math.abs(newX - screenResolution.x)
			// + Math.abs(newY - screenResolution.y);
			int newDiff = Math.abs(size.width - height)
					+ Math.abs(size.height - width);
			if (newDiff < minDiff) {
				targetSize = size;
				minDiff = newDiff;
			}
		}
		return targetSize;
	}

	private Camera.Size getCameraResolutionAPI21(Camera.Parameters param,
			int w, int h) {
		// TODO
		return null;
	}

	Point getCameraResolution() {
		return mCameraResolution;
	}

	/**
	 * Closes the camera driver if still in use.
	 */
	public void closeDriver() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	/**
	 * Asks the camera hardware to begin drawing preview frames to the screen.
	 */
	public void startPreview() {
		if (mCamera != null && !mIsPreviewing) {
			mCamera.startPreview();
			mIsPreviewing = true;
		}
	}

	/**
	 * Tells the camera to stop drawing preview frames.
	 */
	public void stopPreview() {
		if (mCamera != null && mIsPreviewing) {
			mCamera.stopPreview();
			mIsPreviewing = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mPlayer != null) {
			mPlayer.release();
		}
		if (mMediaRecorder != null) {
			mMediaRecorder.release();
		}
	}

}
