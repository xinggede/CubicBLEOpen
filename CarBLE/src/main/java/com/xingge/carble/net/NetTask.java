package com.xingge.carble.net;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络访问类，ondestroy里面一定要setNetListener为null
 */
public class NetTask implements NetProgressListener {

    static HashMap<Object, List<Call>> maps;
    private static OkHttpClient mOkGetHttpClient;
    private static OkHttpClient mOkPostHttpClient;

    static {
        maps = new HashMap<Object, List<Call>>();

        mOkGetHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1000 * 30, TimeUnit.MILLISECONDS)
                .readTimeout(1000 * 30, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                /*.sslSocketFactory(getSSLSocketFactory(), new TrusManage())
                .hostnameVerifier(new Hostname())*//* .cache(cache) */.build();

        // -------------------------------设置http缓存，提升用户体验-----------------------------------
        // mOkGetHttpClient.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);

        mOkPostHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1000 * 30, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .readTimeout(1000 * 30, TimeUnit.MILLISECONDS)
               /* .sslSocketFactory(getSSLSocketFactory(), new TrusManage())
                .hostnameVerifier(new Hostname())*/.build();
    }

    private final String NET_NO_RESULT = "x";
    private NetResultListener mNetListener;
    private NetRequest mRequest;
    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                if (mNetListener != null) {
                    Long times[] = (Long[]) msg.obj;
                    mNetListener.netProgress(mRequest.getCode(), times[0], times[1], mRequest);
                }
            }
        }
    };

    public NetTask(NetResultListener netListener) {
        this.mNetListener = netListener;
    }

    public synchronized static void cancelAll(Object c) {
        try {
            if (c == null) { //清空所有
                for (Object key : maps.keySet()) {
                    List<Call> list = maps.get(key);
                    if (list != null) {
                        for (Call call : list) {
                            if (call != null && !call.isCanceled()) {
                                call.cancel();
                            }
                        }
                    }
                }
                maps.clear();
            } else {  //清空当前activity的请求
                List<Call> list = maps.get(c);
                if (list != null) {
                    for (Call call : list) {
                        if (call != null && !call.isCanceled()) {
                            call.cancel();
                        }
                    }
                }
                maps.remove(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized static void cancelLast(Object c) {
        if (c == null) {
            cancelAll(c);
            return;
        }
        if (maps != null && maps.size() > 0) {
            List<Call> list = maps.get(c);
            if (list != null && list.size() > 0) {
                Call call = list.get(list.size() - 1);
                if (call != null && !call.isCanceled()) {
                    call.cancel();
                    list.remove(call);
                    maps.put(c, list);
                }
            }
        }
    }

    public synchronized static void removeCall(Object c, Call call) {
        if (maps != null && maps.size() > 0 && c != null) {
            List<Call> list = maps.get(c);
            if (list != null && list.size() > 0) {
                if (list.contains(call)) {
                    list.remove(call);
                    if (list.size() == 0) {
                        maps.remove(list);
                    } else {
                        maps.put(c, list);
                    }
                }
            }
        }
    }

    @Override
    public void onProgress(long currentBytes, long contentLength, boolean done) {
        Message.obtain(mMainHandler, 100, new Long[]{currentBytes, contentLength}).sendToTarget();
    }

    public void execute(NetRequest request) {
        this.mRequest = request;
        dealJson();
    }

    private void dealJson() {
        Request request = null;
        Call call = null;
        switch (mRequest.getMethod()) {
            case GET:
                Log.d("cloud", "GET提交>>>" + mRequest.getRequestBodyGET());
                request = new Request.Builder().url(mRequest.getRequestBodyGET())
                        .get().build();
                call = mOkGetHttpClient.newCall(request);
                break;
            case POST:
                request = new Request.Builder().url(mRequest.getURL())
                        .post(/*new ProgressRequestBody(*/mRequest.getRequestBodyPost()/*, this)*/).build();
                call = mOkPostHttpClient.newCall(request);
                break;
            default:
                break;
        }

        if (mRequest.getTag() != null) { //为空不加入请求列表，也就是不能被取消
            List<Call> list = maps.get(mRequest.getTag());
            if (list == null) {
                list = new ArrayList<Call>();
            }
            list.add(call);
            maps.put(mRequest.getTag(), list);
        }

        // 请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                removeCall(mRequest.getTag(), call);
                if (mNetListener == null) {

                } else {
                    // mMainHandler.post(new JsonRunnable(NET_NO_RESULT, 0));
                    if (call.isCanceled()) {
                        mMainHandler.post(new JsonRunnable(NET_NO_RESULT, 101));
                    } else {
                        mMainHandler.post(new JsonRunnable(NET_NO_RESULT, 0));
                    }
                }
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                removeCall(mRequest.getTag(), call);
                if (mNetListener == null) {

                } else {
                    mMainHandler.post(new JsonRunnable(
                            response.body().string(), response.code()));
                }
            }
        });
    }

    class JsonRunnable implements Runnable {
        private String mResult;
        private int mHttpCode;

        public JsonRunnable(String result, int httpCode) {
            Log.d("cloud", result);
            this.mResult = result;
            this.mHttpCode = httpCode;
        }

        @Override
        public void run() {
            if (mHttpCode == 101) {
//                mNetListener.netResultFailed(new ResponseFailed(-1, "已取消请求"),
//                        mRequest);
                return;
            }
            if (NET_NO_RESULT.equals(mResult)) {
                mNetListener.netResultFailed(mRequest.getCode(), mHttpCode, "请求网络失败", mRequest);
                return;
            }

            if (mHttpCode != 200) {
                mNetListener.netResultFailed(mRequest.getCode(), mHttpCode, "服务器维护中", mRequest);
                return;
            }

            try {
                JSONObject jo = new JSONObject(mResult);
                mNetListener.netResultSuccess(mRequest.getCode(), jo, mRequest);
            } catch (Exception e) {
                e.printStackTrace();
                mNetListener.netResultFailed(mRequest.getCode(), mHttpCode, "解析错误", mRequest);
            }

        }
    }
}
