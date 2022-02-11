package com.brotherhood.o2o.ui.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.component.AccountComponent;
import com.brotherhood.o2o.config.Constants;

import java.io.File;
import java.io.IOException;

/**
 * Created by by.huang on 2015/7/20.
 */
public class CaptureDialog extends Dialog implements View.OnClickListener {

    private Activity mContext;
    private Button mTakePhotoBtn;
    private Button mPickImageBtn;
    private Button mCancelBtn;
    private String capturePath;

    public CaptureDialog(Activity context) {
        super(context, R.style.BasicDialog);
        mContext = context;
        setContentView(R.layout.dialog_takephoto);
        setCancelable(true);
        setParams(mContext);
        initView();
    }

    private void initView() {
        mTakePhotoBtn = (Button) findViewById(R.id.btn_takephoto);
        mPickImageBtn = (Button) findViewById(R.id.btn_pickimg);
        mCancelBtn = (Button) findViewById(R.id.btn_cancel);

        mTakePhotoBtn.setOnClickListener(this);
        mPickImageBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == mTakePhotoBtn) {
            doTakePhoto();
        } else if (view == mPickImageBtn) {
            doPickImage();
        }
        dismiss();
    }

    private Uri fileUri = null;

    public void doTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = createFileUri();
        if (fileUri != null) {
            AccountComponent.shareComponent().saveHeadUri(fileUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        }
        mContext.startActivityForResult(intent, Constants.REQUEST_CODE_CAPTURE_CAMEIA);
    }

    private Uri createFileUri() {
        File file = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String saveDir = Environment.getExternalStorageDirectory()
                    + "/temple";
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            file = new File(saveDir, "temp.jpg");
            file.delete();
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    return Uri.fromFile(file);
                } catch (IOException e) {
                    return null;
                }
            }
        }
        return null;
    }

    /***
     * 从相册中选择相片
     */

    public void doPickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        mContext.startActivityForResult(intent, Constants.REQUEST_CODE_PICK_IMAGE);
    }


    private void setParams(Activity context) {
        Window dialogWindow = getWindow();
//        dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth());
        dialogWindow.setAttributes(p);
    }
}
