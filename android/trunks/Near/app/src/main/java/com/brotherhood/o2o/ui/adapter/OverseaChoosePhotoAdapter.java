package com.brotherhood.o2o.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.listener.OnPhotoCheckedListener;
import com.brotherhood.o2o.listener.OnPhotoClickListener;
import com.brotherhood.o2o.manager.ImageLoaderManager;

import java.io.File;
import java.util.List;

/**
 * Created by billy on 2015/12/4.
 */
public class OverseaChoosePhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int VIEW_TYPE_CAMERA = -1;
    private static final int VIEW_TYPE_PHOTO = -2;
    private Context mContext;
    private List<String> mList;
    private OnPhotoCheckedListener mOnPhotoCheckedListener;
    private OnPhotoClickListener mOnPhotoClickListener;
    private SparseBooleanArray mSparseArray = new SparseBooleanArray();//用于保存CheckBox状态
    private boolean onBind;

    public OverseaChoosePhotoAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
        init();
    }

    private void init() {
        for (int i = 0; i < mList.size(); i++) {
            mSparseArray.append(i, false);
        }
    }

    /**
     * 设置选中状态
     *
     * @param position
     */
    public void setChecked(int position) {
        if (position == 0) {
            return;
        }
        if (!onBind) {
            //第一步，清空所有选中按钮
            boolean isCancel = false;//是否是点击选中的按钮
            for (int i = 0; i < mList.size(); i++) {
                if (mSparseArray.get(i)) {
                    mSparseArray.append(i, false);
                    notifyItemChanged(i + 1);
                    if (position - 1 == i) {
                        isCancel = true;
                    }
                }
            }
            if (!isCancel) {//第二步，选中点击的按钮
                mSparseArray.append(position - 1, true);
                notifyItemChanged(position);
            }
        }
    }

    /**
     * 是否有选中的图片
     * @return
     */
    public boolean hasChecked(){
        for (int i = 0; i < mSparseArray.size(); i++) {
            if (mSparseArray.get(i)){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取选中图片路径
     * @return
     */
    public String getCheckedImagePath(){
        if (mList == null || mList.isEmpty()){
            return "";
        }
        for (int i = 0; i < mList.size(); i++) {
            if (mSparseArray.get(i)){
                return mList.get(i);
            }
        }
        return "";
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case VIEW_TYPE_CAMERA:
                View viCamera = LayoutInflater.from(parent.getContext()).inflate(R.layout.i18n_choose_photo_camera_item, null);
                holder = new CameraViewHolder(viCamera);
                break;
            case VIEW_TYPE_PHOTO:
                View viPhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_photo_normal_item, null);
                holder = new PhotoViewHolder(viPhoto);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_CAMERA:
                onBindCameraViewHolder((CameraViewHolder) holder);
                break;
            case VIEW_TYPE_PHOTO:
                onBindPhotoViewHolder((PhotoViewHolder) holder, position);
                break;
        }
    }


    private void onBindPhotoViewHolder(final PhotoViewHolder holder, final int position) {
        onBind = true;
        final String path = mList.get(position - 1);
        ImageLoaderManager.displayImageByFile(mContext, holder.mIvPhoto, new File(path));
        //ImageLoader.getInstance().setImageLocal(holder.mIvPhoto, path, DisplayUtil.dp2px(88), DisplayUtil.dp2px(88));
        holder.mCheckBox.setChecked(mSparseArray.get(position - 1));
        holder.mIvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPhotoClickListener != null) {
                    mOnPhotoClickListener.onPhotoClick(holder.mIvPhoto, position);
                }

            }
        });
        holder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mOnPhotoCheckedListener != null) {
                    mOnPhotoCheckedListener.onCheckedChanged(position);
                }

            }
        });
        onBind = false;
    }

    private void onBindCameraViewHolder(final CameraViewHolder holder) {
        holder.mLlCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPhotoClickListener != null) {
                    mOnPhotoClickListener.onPhotoClick(null, 0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mList == null || mList.isEmpty()) {
            return 1;
        }
        return mList.size() + 1;
    }

    public void setOnPhotoCheckedListener(OnPhotoCheckedListener listener) {
        mOnPhotoCheckedListener = listener;
    }

    public void setOnPhotoClickListener(OnPhotoClickListener listener) {
        mOnPhotoClickListener = listener;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_CAMERA;
        }
        return VIEW_TYPE_PHOTO;
    }

    private static class CameraViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLlCamera;

        public CameraViewHolder(View itemView) {
            super(itemView);
            mLlCamera = (LinearLayout) itemView.findViewById(R.id.llChooseItemCamera);
        }
    }

    private static class PhotoViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvPhoto;
        private CheckBox mCheckBox;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            mIvPhoto = (ImageView) itemView.findViewById(R.id.ivChooseItemPhoto);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.cbChooseItemCheckbox);
        }
    }
}
