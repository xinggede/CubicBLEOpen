package com.xing.sd.bluetooth;

import com.xing.sd.bean.CurrentImageInfo;

/**
 * @ClassName UpdateCallback
 * @Description TODO
 * @Author 星哥的
 * @Date 2023/12/25 14:25
 * @Version 1.0
 */
public interface UpdateCallback {

    void onCurrentImageInfo(CurrentImageInfo currentImageInfo);

    void onUpdate(boolean success, int type, String msg, int progress, int total);
}
