package com.xing.sd.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by 星哥的 on 2018/3/22.
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(getLayoutId(), container, false);
        initView(mView, savedInstanceState);
        return mView;
    }

    protected abstract int getLayoutId();

    protected abstract View initView(View view, Bundle savedInstanceState);

    @Override
    public void onClick(View v) {

    }
}
