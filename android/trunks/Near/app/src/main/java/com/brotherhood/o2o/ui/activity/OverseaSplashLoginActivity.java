package com.brotherhood.o2o.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.config.SharePrefConstant;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.manager.DefaultSharePrefManager;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.bean.FBUserInfo;
import com.brotherhood.o2o.util.DeviceUtil;
import com.brotherhood.o2o.util.FacebookLoginUtil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

public class OverseaSplashLoginActivity extends BaseActivity {

    /*@ViewInject(id = R.id.llLoginWithFacebook, clickMethod = "onFacebookLogin")
    private LinearLayout mLlLoginFacebook;*/

    @ViewInject(id = R.id.llEmailRegist, clickMethod = "onEmailRegist")
    private LinearLayout mLlLoginEmail;

    @ViewInject(id = R.id.tvRegisteredLogin, clickMethod = "onRegisteredLogin")
    private TextView mTvRegisteredLogin;

    @ViewInject(id = R.id.toDomestic, clickMethod = "toDomestic")
    private Button mToDomestic;

    /*facebook callbackManager to handle login responses*/
    CallbackManager callbackManager;
    private static String TAG = "FacebookLogin";
    private static FacebookLoginUtil mFaceBookLoginUtil = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.

        //mFaceBookLoginUtil = FacebookLoginUtil.getInstance();
        // mFaceBookLoginUtil.open();

        callbackManager = CallbackManager.Factory.create();


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {


            /**
             * 成功获得授权，拿到了accessToken 现在，有授权后，在这里去拉取信息
             * @param loginResult
             */
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Toast.makeText(getApplication(), "facebook_account_oauth_Success", Toast.LENGTH_SHORT).show();

                Log.e(TAG, "token: " + loginResult.getAccessToken().getToken());
                Toast.makeText(getApplication(), loginResult.getAccessToken().getToken() + "111", Toast.LENGTH_SHORT).show();

                fetchUserInfo(loginResult.getAccessToken());

                //---------------------------------



            /*    GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                Log.e("LoginActivity", response.toString());
                            }
                        });


                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();

                //2016.1.7----------------------------------------------------------
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/{user-id}",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
            *//* handle the result *//*
                                Log.e("photo", response.toString());
                            }
                        }
                ).executeAsync();*/

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplication(), "facebook_account_oauth_Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplication(), "facebook_account_oauth_Error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "e: " + e);
            }
        });

        mTvRegisteredLogin.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        mTvRegisteredLogin.getPaint().setAntiAlias(true);//抗锯齿
    }


    private void fetchUserInfo(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,
                                            GraphResponse response) {
                        try {
                            Log.e("fetchUserInfo11", response.toString());
                            if (response.getError() != null) {
                                //Error
                                Toast.makeText(getApplication(), "getError", Toast.LENGTH_LONG).show();
                            } else if (response.getConnection().getResponseCode() == 200) {
                                FBUserInfo user = new FBUserInfo();
                                //  user.setEmail(object.getString("email"));
                                /*user.setGender(object.getString("gender"));
                                user.setLink(object.getString("link"));
                                user.setFirstname(object.getString("first_name"));
                                user.setLastname(object.getString("last_name"));
                                user.setLocale(object.getString("locale"));
                                user.setTimezone(object.getString("timezone"));
                                user.setUserId(object.getString("id"));
                                user.setUserName(object.getString("name"));*/

                                String id = object.getString("id");

                                new GraphRequest(
                                        AccessToken.getCurrentAccessToken(),
                                        "/"+id+"/picture",  //
                                        null,
                                        HttpMethod.GET,
                                        new GraphRequest.Callback() {
                                            public void onCompleted(GraphResponse response) {
                                     /* handle the result */
                                                Log.e("CoverPhoto", response.toString());
                                                try {
                                                     response.getJSONObject().get("data");
                                                    new GraphRequest(
                                                            AccessToken.getCurrentAccessToken(),
                                                            "...?fields="+ response.getJSONObject().get("data"),
                                                            null,
                                                            HttpMethod.GET,
                                                            new GraphRequest.Callback() {
                                                                public void onCompleted(GraphResponse response) {
                                                                    try {
                                                                        String url = (String) response.getJSONObject().get("url");
                                                                        Log.e("url", url);
                                                                    } catch (Exception e) {

                                                                    }
                                                                }
                                                            }
                                                    ).executeAsync();

                                                } catch (Exception e) {

                                                }
                                            }
                                        }
                                ).executeAsync();

                                //TODO  传递封装信息
                                Toast.makeText(getApplication(), object.toString() + "...", Toast.LENGTH_LONG).show();
                                System.out.println(object.toString() + "----------------------------------------------------------");
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplication(), "错误", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        request.executeAsync();
    }

    /*获取facebook登录返回信息*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_oversea_splash_login;
    }


    /**
     * Facebook login
     *
     * @param view
     */
   /* public void onFacebookLogin(View view) {

        if (view.getId() == R.id.llLoginWithFacebook) {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken == null || accessToken.isExpired()) {
                //没有accessToken或者accessToken过期，则去facebook页面获得授权---获取的是accessToken吗？
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday", "user_photos"));
            }
        }
    }*/

    /**
     * Email注册
     *
     * @param view
     */
    public void onEmailRegist(View view) {
        Intent intent = new Intent(this, EmailRegistActivity.class);
        startActivity(intent);
    }

    /**
     * 已注册用户登陆
     *
     * @param view
     */
    public void onRegisteredLogin(View view) {

        Intent intent = new Intent(this, EmailLoginActivity.class);
        startActivity(intent);

    }

    public static void show(Context context) {
        Intent intent = new Intent(context, OverseaSplashLoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转国内版
     * @param view
     */
    public void toDomestic(View view) {
        DefaultSharePrefManager.putBoolean(SharePrefConstant.LOGIN_VERSION, false);
        DeviceUtil.rebootApp(OverseaSplashLoginActivity.this);
    }
}
