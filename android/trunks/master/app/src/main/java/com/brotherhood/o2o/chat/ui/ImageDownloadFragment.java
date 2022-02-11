package com.brotherhood.o2o.chat.ui;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.utils.Utils;
import com.skynet.library.message.MessageManager;

public class ImageDownloadFragment extends Fragment {

	private ImageView mImageView;

	private ProgressBar mProgressBar;
	private TextView mProgressText;

	public static final String ARGS_THUMB_PATH = "thumb_path";
	public static final String ARGS_FILE_PATH = "file_path";
	private File mDoneFile;
	private File mDeletedFile;

	public ImageDownloadFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.chat_img_browse, container, false);
		initView(v);
		return v;
	}

	private void initView(View v) {
		mImageView = (ImageView) v.findViewById(R.id.chat_browse_image);
		mProgressBar = (ProgressBar) v.findViewById(R.id.chat_download_pb);
		mProgressText = (TextView) v
				.findViewById(R.id.chat_download_progress_text);

		if (!Utils.isSdcardWritable(getActivity())) {
			setDownloadWidgetsGone();
			mImageView.setImageResource(R.drawable.chat_img_large_no_sdcard);
			return;
		}

		Bundle bundle = getArguments();
		String largeFilePath = bundle.getString(ARGS_FILE_PATH);
		final File largeFile = new File(largeFilePath);
		File parent = largeFile.getParentFile();
		mDoneFile = new File(parent, largeFile.getName() + ".done");
		if (mDoneFile.exists()) {
			if (largeFile.exists()) {
				try {
					Bitmap bmp = BitmapFactory.decodeFile(largeFilePath);
					if (bmp != null) {
						mImageView.setImageBitmap(bmp);
						setDownloadWidgetsGone();
						return;
					}
				} catch (OutOfMemoryError e) {
					// REPLACEME
					mImageView
							.setImageResource(R.drawable.chat_img_large_no_sdcard);
					showToast(R.string.chat_can_not_show_img);
				}
			} else {
				setDownloadWidgetsGone();
				mImageView.setImageResource(R.drawable.chat_img_large_deleted);
				showToast(R.string.chat_img_not_found_or_ot);
			}
			return;
		}
		mDeletedFile = new File(parent, largeFile.getName() + ".deleted");
		if (mDeletedFile.exists()) {
			setDownloadWidgetsGone();
			mImageView.setImageResource(R.drawable.chat_img_large_deleted);
			return;
		}

		String thumbPath = bundle.getString(ARGS_THUMB_PATH);
		File thumbFile = new File(thumbPath);
		if (thumbFile.exists()) {
			Bitmap bmp = BitmapFactory.decodeFile(thumbPath);
			if (bmp != null) {
				mImageView.setImageBitmap(bmp);
			}
		}

		MessageManager mm = MessageManager.getDefault(getActivity());
		mm.downloadFile(largeFile.getName(), MessageManager.FILE_TYPE_IMAGE,
				new MessageManager.FileDownloadListener() {

					@Override
					public void onProgressChanged(final int progress) {
						mProgressText.setText(String.valueOf(progress));
					}

					@Override
					public void onDownloadFinished(int code, byte[] content) {
						setDownloadWidgetsGone();
						if (code == MessageManager.FileDownloadListener.CODE_FAILED) {
							showToast(R.string.chat_download_img_io);
						} else if (code == MessageManager.FileDownloadListener.CODE_FILE_DELETED_FROM_SERVER) {
							try {
								mDeletedFile.createNewFile();
							} catch (IOException e) {
							}
						} else {
							BitmapFactory.Options opt = new BitmapFactory.Options();
							opt.inJustDecodeBounds = true;
							Bitmap bmp = BitmapFactory.decodeByteArray(content,
									0, content.length, opt);
							if (opt.outWidth <= 0 || opt.outHeight <= 0) {
								// The data can not be decoded into a bitmap
								// which should never happen
								mImageView
										.setImageResource(R.drawable.chat_voice_download_error);
								return;
							}
							// Whenever the data can be decoded into a bitmap,
							// we write the data to the target thumb file
							Utils.writeFile(largeFile, content);
							try {
								mDoneFile.createNewFile();
							} catch (IOException e) {
							}

							opt.inJustDecodeBounds = false;
							// Try again the decode the bitmap
							bmp = BitmapFactory.decodeByteArray(content, 0,
									content.length, opt);
							if (bmp != null) {
								mImageView.setImageBitmap(bmp);
							} else {
								mImageView
										.setImageResource(R.drawable.chat_voice_download_error);
							}
							try {
								mDoneFile.createNewFile();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				});
	}

	private void showToast(int resId) {
		Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
	}

	private void setDownloadWidgetsGone() {
		mProgressBar.setVisibility(View.GONE);
		mProgressText.setVisibility(View.GONE);
	}

}
