package com.xing.sd.ui.mode;


import android.content.Context;

/**
 * @author 星哥的
 */
public class SearchModel extends BleModel implements SearchContract.Model {

    public static final String PWD_SID = "ffc0";
    public static final String PWD_WCID = "ffc1";
    public static final String PWD_RCID = "ffc2";

    public SearchModel(Context context) {
        super(context);
    }



}
