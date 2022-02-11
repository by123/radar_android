package com.brotherhood.o2o.util;


import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.brotherhood.o2o.bean.FBUserInfo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
//监听
//import com.tibco.integeration.login.listener.LogInStateListener;
//import com.tibco.integeration.login.listener.LogOutStateListener;
//import com.tibco.integration.login.model.User;


/**
 * Created by billy.shi on 2015/12/31.
 */
public class FacebookLoginUtil {
    private  static FacebookLoginUtil mFaceBookLoginUtil=null;
    private static Activity loginActivity=null;
    private View loginClickView=null;
    private View logOutView=null;


    private  List<String> permissions;
    private  LogInStateListener mBookLoginStateChanged;
    private LogOutStateListener mBookLogOutStateListener;
    private CallbackManager callbackManager;

    private FaceBookCallBackListener callback=new FaceBookCallBackListener();
    private  OnFaceBookLoginClickListener onFaceBookLoginClickListener=new OnFaceBookLoginClickListener();
    private boolean isLogOut=false;


    public static FacebookLoginUtil getInstance() {
        if(mFaceBookLoginUtil==null){
            mFaceBookLoginUtil=new FacebookLoginUtil();
        }
        return mFaceBookLoginUtil;
    }
    public  void SetFaceBookLoginActivity(Activity activity){
        if(activity==null){
            throw new  NullPointerException("login activity is null");
        }else{
            loginActivity=activity;
        }
    }

    public  void SetFaceBookLoginButton(View view){
        loginClickView=view;
    }

    public void SetFaceBookLogOutButton(View view){
        logOutView=view;
    }

    public  void SetFaceBookReadPermission(String  array){
        if(array==null){
            permissions=Arrays.asList("public_profile");
        }else{
            permissions=Arrays.asList(array);
        }
    }

    /**
     * 登陆
     * @param LoginStateChanged
     */
    public  void SetOnFaceBookLoginStateListener(LogInStateListener LoginStateChanged){
        if(LoginStateChanged==null){
            throw new NullPointerException("LoginStateListener is null");
        }else{
            mBookLoginStateChanged=LoginStateChanged;
        }
    }

    /**
     * 退出登陆
     * @param logoutListener
     */
    public void SetOnFaceBookLogOutListener(LogOutStateListener logoutListener){
        mBookLogOutStateListener=logoutListener;
    }

    /**
     * 初始化facebookＳＤＫ，onCreate中最先调用
     */
    public void open() {
        FacebookSdk.sdkInitialize(loginActivity);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, callback);

        if(loginClickView!=null){
            loginClickView.setOnClickListener(onFaceBookLoginClickListener);
        }
        if(logOutView!=null){
            logOutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginManager.getInstance().logOut();
                    isLogOut=true;
                    mBookLogOutStateListener.OnLogOutListener(isLogOut,"facebook");
                }
            });
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

   /* public void OnDestory(){
        ProfileTracker.stopTracking();
    }
    */


    private class FaceBookCallBackListener implements FacebookCallback<LoginResult>{
        @Override
        public void onSuccess(LoginResult result) {
            fetchUserInfo(result.getAccessToken());
        }
        @Override
        public void onCancel() {
            mBookLoginStateChanged.OnLoginError("user cancle log in facebook!");
        }
        @Override
        public void onError(FacebookException error) {
            mBookLoginStateChanged.OnLoginError(error.getMessage());
        }

    }

    /**
     * 点击登陆获取facebook 用户信息
     */
    private  class OnFaceBookLoginClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            LoginManager.getInstance().logInWithReadPermissions(loginActivity,permissions);
        }
    }



    private void fetchUserInfo(AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,
                                            GraphResponse response) {
                        try{
                            if(response.getError()!=null){
                                //mBookLoginStateChanged.OnLoginError(<span style="font-family: Arial, Helvetica, sans-serif;">response.getError()..getErrorMessage()</span>);
                            }else if(response.getConnection().getResponseCode()==200){
                                FBUserInfo user=new FBUserInfo();
                                user.setEmail(object.getString("email"));
                                user.setGender(object.getString("gender"));
                                user.setLink(object.getString("link"));
                                user.setFirstname(object.getString("first_name"));
                                user.setLastname(object.getString("last_name"));
                                user.setLocale(object.getString("locale"));
                                user.setTimezone(object.getString("timezone"));
                                user.setUserId(object.getString("id"));
                                user.setUserName(object.getString("name"));
                               // mBookLoginStateChanged.OnLoginSuccess(user, "facebook");
                            }
                        }catch(Exception e){
                            mBookLoginStateChanged.OnLoginError(e.getMessage());
                        }
                    }
                });
        request.executeAsync();
    }


    /**
     * 登陆成功的回调函数
     */
    public interface LogInStateListener {
        public void OnLoginSuccess(FBUserInfo user,String logType);
        public void OnLoginError(String error);
    }

    /**
     * 退出登陆的回调函数
     */
    public interface LogOutStateListener {
        public void OnLogOutListener(boolean isSuccess, String logOutType);
    }
}
