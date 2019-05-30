package com.xingge.carble.net;


import org.json.JSONObject;

public interface NetResultListener {

    void netResultSuccess(int what, JSONObject object, NetRequest request);

    void netResultFailed(int what, int mHttpCode, String msg, NetRequest request);

    void netProgress(int what, long bytesWritten, long totalSize, NetRequest request);
}
