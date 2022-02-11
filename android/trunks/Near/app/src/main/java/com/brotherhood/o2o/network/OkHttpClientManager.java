package com.brotherhood.o2o.network;

import android.os.Build;

import com.alibaba.fastjson.JSON;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.manager.IDSEnvManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.task.TaskExecutor;
import com.brotherhood.o2o.util.DeviceUtil;
import com.skynet.library.login.net.LoginManager;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with Android Studio
 */
public class OkHttpClientManager {
    private static OkHttpClientManager mInstance;
    private OkHttpClient mOkHttpClient;

    private GetDelegate mGetDelegate = new GetDelegate();
    private UploadDelegate mUploadDelegate = new UploadDelegate();
    private PostDelegate mPostDelegate = new PostDelegate();

    private OkHttpClientManager() {
        mOkHttpClient = new OkHttpClient();
        //cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
    }

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    public UploadDelegate _getUploadDelegate() {
        return mUploadDelegate;
    }


    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private void deliveryResult(final ResultCallback callback, Request request) {
        //UI thread
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                if (callback != null) {
                    sendFailedStringCallback(request, e, callback);
                }
            }

            @Override
            public void onResponse(final Response response) {
                if (callback != null) {
                    try {
                        final String string = response.body().string();
                        if (callback.mType == String.class) {
                            sendSuccessResultCallback(string, callback);
                        } else {
                            Type genType = getClass().getGenericSuperclass();
                            Type trueType = ((ParameterizedType) genType).getActualTypeArguments()[0];
                            sendSuccessResultCallback(JSON.parseObject(string, trueType), callback);
                        }

                    } catch (IOException e) {
                        LogManager.e(e);
                        sendFailedStringCallback(response.request(), e, callback);
                    }
                }

            }
        });
    }

    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback) {
        TaskExecutor.runTaskOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onError(request, e);
            }
        });
    }

    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
        TaskExecutor.runTaskOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(object);
            }
        });
    }

    private Request buildPostFormRequest(String url, Map<String, String> params, Object tag) {
        if (params == null) {
            params = new HashMap<>();
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();

        StringBuilder stringBuilder = new StringBuilder();

        Set<Map.Entry<String, String>> entries = params.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            builder.add(entry.getKey(), entry.getValue());
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), stringBuilder.toString());
        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.url(url).post(requestBody);

        if (tag != null) {
            reqBuilder.tag(tag);
        }
        return reqBuilder.build();
    }

    public void _cancelTag(Object tag) {
        mOkHttpClient.cancel(tag);
    }

    public abstract class ResultCallback<T> {
        Type mType;

        public ResultCallback() {
            mType = getSuperclassTypeParameter(getClass());
        }

        Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            Type type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
            return type;
        }

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(T response);
    }

    //====================PostDelegate=======================
    public class PostDelegate {
        private final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream;charset=utf-8");
        private final MediaType MEDIA_TYPE_STRING = MediaType.parse("text/plain;charset=utf-8");

        public Response post(String url, Map<String, String> params) throws IOException {
            return post(url, params, null);
        }

        /**
         * 同步的Post请求
         */
        public Response post(String url, Map<String, String> params, Object tag) throws IOException {
            Request request = buildPostFormRequest(url, params, tag);
            return mOkHttpClient.newCall(request).execute();
        }

        /**
         * 同步的Post请求:直接将bodyStr以写入请求体
         */
        public Response post(String url, String bodyStr) throws IOException {
            return post(url, bodyStr, null);
        }

        public Response post(String url, String bodyStr, Object tag) throws IOException {
            RequestBody body = RequestBody.create(MEDIA_TYPE_STRING, bodyStr);
            Request request = buildPostRequest(url, body, tag);
            return mOkHttpClient.newCall(request).execute();
        }

        /**
         * 同步的Post请求:直接将bodyFile以写入请求体
         */
        public Response post(String url, File bodyFile) throws IOException {
            return post(url, bodyFile, null);
        }

        public Response post(String url, File bodyFile, Object tag) throws IOException {
            RequestBody body = RequestBody.create(MEDIA_TYPE_STREAM, bodyFile);
            Request request = buildPostRequest(url, body, tag);
            return mOkHttpClient.newCall(request).execute();
        }

        /**
         * 同步的Post请求
         */
        public Response post(String url, byte[] bodyBytes) throws IOException {
            return post(url, bodyBytes, null);
        }

        public Response post(String url, byte[] bodyBytes, Object tag) throws IOException {
            RequestBody body = RequestBody.create(MEDIA_TYPE_STREAM, bodyBytes);
            Request request = buildPostRequest(url, body, tag);
            return mOkHttpClient.newCall(request).execute();
        }

        /**
         * post构造Request的方法
         *
         * @param url
         * @param body
         * @return
         */
        private Request buildPostRequest(String url, RequestBody body, Object tag) {
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .post(body);
            if (tag != null) {
                builder.tag(tag);
            }
            return builder.build();
        }
    }

    //====================GetDelegate=======================
    public class GetDelegate {

        private Request buildGetRequest(String url, Object tag) {
            Request.Builder builder = new Request.Builder()
                    .url(url);
            if (tag != null) {
                builder.tag(tag);
            }
            return builder.build();
        }

        /**
         * 通用的方法
         */
        public Response get(Request request) throws IOException {
            Call call = mOkHttpClient.newCall(request);
            return call.execute();
        }

        /**
         * 同步的Get请求
         */
        public Response get(String url) throws IOException {
            return get(url, null);
        }

        public Response get(String url, Object tag) throws IOException {
            final Request request = buildGetRequest(url, tag);
            return get(request);
        }

        /**
         * get请求
         */
        public void getAsyn(Request request, ResultCallback callback) {
            deliveryResult(callback, request);
        }

    }

    /**
     * 上传相关的模块
     */
    public class UploadDelegate {
        /**
         * 同步基于post的文件上传，上传单个文件
         */
        public Response post(String url, String fileKey, File file, Object tag) throws IOException {
            return post(url, new String[]{fileKey}, new File[]{file}, null, tag);
        }

        /**
         * 同步基于post的文件上传，上传多个文件以及携带key-value对：主方法
         */
        public Response post(String url, String[] fileKeys, File[] files, Map<String, String> params, Object tag) throws IOException {
            Request request = buildMultipartFormRequest(url, files, fileKeys, params, tag);
            return mOkHttpClient.newCall(request).execute();
        }

        /**
         * 同步单文件上传，带其他参数
         */
        public Response post(String url, String fileKey, File file, Map<String, String> params, Object tag) throws IOException {
            return post(url, new String[]{fileKey}, new File[]{file}, params, tag);
        }

        /**
         * 异步基于post的文件上传，带其他参数
         */
        public void postAsyn(String url, String[] fileKeys, File[] files, Map<String, String> params, ResultCallback callback, Object tag) {
            Request request = buildMultipartFormRequest(url, files, fileKeys, params, tag);
            deliveryResult(callback, request);
        }


        /**
         * 异步基于post的文件上传，带网关
         */
        public void postAsynWithHead(String url, String[] fileKeys, File[] files, Map<String, String> params, ResultCallback callback, Object tag) {
            Request request = buildMultipartFormRequestWithHead(url, files, fileKeys, params, tag);
            deliveryResult(callback, request);
        }

        /**
         * 异步基于post的文件上传，单文件且携带其他form参数上传
         */
        public void postAsyn(String url, String fileKey, File file, Map<String, String> params, ResultCallback callback, Object tag) {
            postAsyn(url, new String[]{fileKey}, file == null?null:new File[]{file}, params, callback, tag);
        }

        /**
         * 异步基于post的文件上传，单文件且携带其他form参数上传，带网关
         */
        public void postAsynWithHead(String url, String fileKey, File file, Map<String, String> params, ResultCallback callback, Object tag) {
            postAsynWithHead(url, new String[]{fileKey}, file == null?null:new File[]{file}, params, callback, tag);
        }


        private Request buildMultipartFormRequestWithHead(String url, File[] files, String[] fileKeys, Map<String, String> params, Object tag) {
            if (params == null)
                params = new HashMap<>();
            MultipartBuilder builder = new MultipartBuilder()
                    .type(MultipartBuilder.FORM);

            Set<Map.Entry<String, String>> entries = params.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                        RequestBody.create(null, entry.getValue()));
            }

            if (files != null) {
                RequestBody fileBody;
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    String fileName = file.getName();
                    fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                    builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                            fileBody);
                }
            }
            String header = LoginManager.getInstance().generateOAuthHeader("POST", url);
            int versionCode = DeviceUtil.getVersionCode(NearApplication.mInstance);
            String channel = IDSEnvManager.getInstance().getChannel();
            String brand = Build.BRAND;
            String OsversionName = "Android"+Build.VERSION.RELEASE;
            String phoneType = DeviceUtil.getPhoneType();
            String buildVersionCode = DeviceUtil.getVersionName(NearApplication.mInstance);
            String userAgentValue = "Near/"+ versionCode +" ("+ brand +"; "+ OsversionName +"; Scale/2.00) | channel="+ channel +"&version="+
                    versionCode
                    +"&phoneType="+ phoneType
                    +"&buildVersion="+ buildVersionCode;
            RequestBody requestBody = builder.build();
            return new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", header)//增加网关(请求头)
                    .addHeader("User-Agent", userAgentValue)//增加网关(请求头)
                    .post(requestBody)
                    .tag(tag)
                    .build();
        }

        /**
         * 以表单的形式做文件上传
         *
         * @param url
         * @param files
         * @param fileKeys
         * @param params
         * @param tag
         * @return
         */
        private Request buildMultipartFormRequest(String url, File[] files, String[] fileKeys, Map<String, String> params, Object tag) {
            if (params == null)
                params = new HashMap<>();
            MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);

            Set<Map.Entry<String, String>> entries = params.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                        RequestBody.create(null, entry.getValue()));
            }

            if (files != null) {
                RequestBody fileBody;
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    String fileName = file.getName();
                    fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                    builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                            fileBody);
                }
            }

            RequestBody requestBody = builder.build();
            return new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .tag(tag)
                    .build();
        }

    }
}

