package com.brotherhood.o2o.manager;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.brotherhood.o2o.bean.Member;
import com.brotherhood.o2o.bean.UserInfoBean;
import com.brotherhood.o2o.chat.db.service.IMDBGroupService;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.GetAvatarRequest;
import com.brotherhood.o2o.service.AvatarDownloadService;

import java.io.File;
import java.util.List;

/**
 * Created by laimo.li on 2015/12/28.
 */
public class GroupAvatarManager {

    private static GroupAvatarManager instance;

    public static GroupAvatarManager getInstance() {
        if (instance == null) {
            instance = new GroupAvatarManager();
        }
        return instance;
    }


    public void loadActivitiesConferenceAvatar(String gid, ImageView imageView) {


    }


    /**
     * 获取群头像
     *
     * @param context
     * @param gid
     * @param members
     * @param imageView
     */
    public void loadGroupAvatar(final Context context, final String gid, final List<Member> members, final ImageView imageView, final CallBack callBack) {

        IMDBGroupService.queryGroupAvatar(gid, new IMDBGroupService.DBListener() {
            @Override
            public void onResult(Object obj) {
                String avatar = (String) obj;
                if (!TextUtils.isEmpty(avatar) && new File(avatar).exists()) {
                    callBack.avatar(avatar);
                    //ImageLoaderManager.displayCircleImageByUrl(context, imageView, avatar, R.mipmap.ic_msg_default);
                } else {
                    avatar = DirManager.getExternalStroageDir(Constants.HTTP_CACHE)+ "/" + gid + ".png";
                    File file = new File(avatar);
                    if (file.exists()) {
                        callBack.avatar(avatar);
                    } else {
                        downAvatart(context, gid, members, imageView, callBack);
                    }
                }

            }
        });

    }

    private void downAvatart(final Context context, final String gid, final List<Member> members, final ImageView imageView, final CallBack callBack) {
        StringBuffer sb = new StringBuffer();
        if (members == null) {
            sb.append(AccountManager.getInstance().getUser().mUid);
        } else {
            int size = members.size();
            String uid = AccountManager.getInstance().getUser().mUid;
            if (size == 1) { //群人数为1,显示自己头像
                sb.append(uid);
            } else {
                for (int i = 0; i < size; i++) {
                    Member member = members.get(i);
                    if (size >= 3) {  //群人数大于等于3，显示前三人头像
                        sb.append(member.getUid());
                        if (i != size) {
                            sb.append(",");
                        }
                    } else if (size == 2) { //群人数为2，显示对方头像
                        if (!uid.equals(member.getUid())) {
                            sb.append(member.getUid());
                        }
                    }
                }
            }
        }

        GetAvatarRequest request = GetAvatarRequest.createAvatarRequest(sb.toString(), new OnResponseListener<List<UserInfoBean>>() {
            @Override
            public void onSuccess(int code, String msg, List<UserInfoBean> avatarBeans, boolean cache) {
                if (avatarBeans.size() == 0) {
                    return;
                }
                if (avatarBeans.size() == 1) {
                    callBack.avatar(avatarBeans.get(0).getAvatar());
                    //ImageLoaderManager.displayCircleImageByUrl(context, imageView, avatarBeans.get(0).getAvatar(), R.mipmap.ic_msg_default);
                } else {
                    createGroupAvatar(context, gid, avatarBeans, imageView, callBack);
                }
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
        request.sendRequest();
    }

    private void createGroupAvatar(final Context context, String gid, List<UserInfoBean> avatarBeans, final ImageView imageView, final CallBack callBack) {
        new AvatarDownloadService(gid, avatarBeans, new AvatarDownloadService.DownloadStateListener() {
            @Override
            public void onFinish(final String iamgePath) {
                callBack.avatar(iamgePath);
                //ImageLoaderManager.displayCircleImageByUrl(context, imageView, iamgePath, R.mipmap.ic_msg_default);
            }

            @Override
            public void onFailed() {

            }
        }

        ).startDownload();
    }


    public String getAvatar(long gid) {
        String avatarPath = DirManager.getExternalStroageDir(Constants.HTTP_CACHE)+ "/" + gid + ".png";
        File file = new File(avatarPath);
        if (file.exists()) {
            return avatarPath;
        }
        return "";
    }

    public interface CallBack {
        void avatar(String avatar);
    }


}
