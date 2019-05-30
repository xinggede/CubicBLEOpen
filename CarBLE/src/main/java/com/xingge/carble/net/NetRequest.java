package com.xingge.carble.net;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 请求封装类
 */
public class NetRequest {
    public enum METHOD {
        POST, GET
    }

    private METHOD mMethod = METHOD.GET;
    private int mCode;
    private boolean mShouldRepeat;
    private int mMaxRepeat = 3;
    private Object response;
    private Object mTag;
    // 控制请求时候是不是显示进度对话框以及是否处理成功返回界面UI的更新
    private boolean isShowPro = true;// 默认加载进度条
    private Map<String, Object> params;
    private String baseUrl/* = MyConstants.GET_BASE_URL*/;
    private String childUrl;

    private String paramsJson = null;

    public NetRequest(int code, String childUrl, String json) {
        this.mCode = code;
        this.childUrl = childUrl;
        this.mMethod = METHOD.POST;
        this.paramsJson = json;
    }

    public NetRequest(int code, String childUrl, Map<String, Object> params, METHOD method) {
        this.params = params;
        this.mCode = code;
        this.childUrl = childUrl;
        this.mMethod = method;
    }

    public NetRequest(int code, String childUrl, String json, METHOD method) {
        this.paramsJson = json;
        this.mCode = code;
        this.childUrl = childUrl;
        this.mMethod = method;
    }

    public NetRequest(int code, String childUrl, Map<String, Object> params, METHOD method, boolean isShow, boolean shouldRepeat) {
        this.params = params;
        this.mCode = code;
        this.childUrl = childUrl;
        this.mMethod = method;
        this.isShowPro = isShow;
        this.mShouldRepeat = shouldRepeat;
    }

    public NetRequest buildShowPro() {
        this.isShowPro = true;
        return this;
    }

    public NetRequest buildNoShowPro() {
        this.isShowPro = false;
        return this;
    }

    public RequestBody getRequestBodyPost() {
        if (!TextUtils.isEmpty(paramsJson)) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramsJson);
            Log.d("cloud", "POST提交>>>" + getURL() + "?" + paramsJson);
            return body;
        }
        FormBody.Builder formBody = new FormBody.Builder(); //表单

        MultipartBody.Builder builder = new MultipartBody.Builder(); //文件
        StringBuilder sb = new StringBuilder();
//        //设置类型
        builder.setType(MultipartBody.FORM);
        boolean isFile = false;
        //追加参数
        for (String key : params.keySet()) {
            Object object = params.get(key);
            if (object instanceof File) {
                isFile = true;
                File file = (File) object;
                builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
                sb.append(key).append("=").append(file.getAbsolutePath()).append("&");
            } else if (object instanceof List) {
                isFile = true;
                List<File> files = (List<File>) object;
                for (File file : files) {
                    builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
                    sb.append(key).append("=").append(file.getAbsolutePath()).append("&");
                }
            } else {
                builder.addFormDataPart(key, String.valueOf(object));
                formBody.add(key, String.valueOf(object));
                sb.append(key).append("=").append(object.toString()).append("&");
            }
        }
        Log.d("cloud", "POST提交>>>" + getURL() + "?" + sb.toString());
        return isFile ? builder.build() : formBody.build();
    }

    public String getRequestBodyGET() {
        if (params == null) {
            return getURL();
        }
        StringBuilder builder = new StringBuilder();
        for (String key : params.keySet()) {
            Object value = params.get(key);
            builder.append(key);
            builder.append("=");
            builder.append(value);
            builder.append("&");
        }
        String request = builder.toString();
        if (request.length() != 0) {
            request = request.substring(0, request.length() - 1);
        }
        return getURL() + "?" + request;
    }

    public String getURL() {
        return TextUtils.isEmpty(baseUrl) ? childUrl : baseUrl + childUrl;
    }

    public METHOD getMethod() {
        return mMethod;
    }

    public NetRequest setMethod(METHOD mMethod) {
        this.mMethod = mMethod;
        return this;
    }


    public int getCode() {
        return mCode;
    }

    public void setCode(int mCode) {
        this.mCode = mCode;
    }

    public boolean isShouldRepeat() {
        return mShouldRepeat;
    }

    public void setShouldRepeat(boolean mShouldRepeat) {
        this.mShouldRepeat = mShouldRepeat;
    }

    public int getMaxRepeat() {
        return mMaxRepeat;
    }

    public void setMaxRepeat(int mMaxRepeat) {
        this.mMaxRepeat = mMaxRepeat;
    }


    public Object getTag() {
        return mTag;
    }

    public void setTag(Object mTag) {
        this.mTag = mTag;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public boolean isShowPro() {
        return isShowPro;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getChildUrl() {
        return childUrl;
    }

    public void setChildUrl(String childUrl) {
        this.childUrl = childUrl;
    }
}
