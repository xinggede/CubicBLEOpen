package com.xingge.carble.net;

public interface NetProgressListener {

    void onProgress(long currentBytes, long contentLength, boolean done);
}
