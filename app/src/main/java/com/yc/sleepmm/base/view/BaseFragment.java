package com.yc.sleepmm.base.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hwangjr.rxbus.RxBus;
import com.kk.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;
import com.yc.sleepmm.base.presenter.BasePresenter;
import com.yc.sleepmm.base.util.EmptyUtils;

import butterknife.ButterKnife;

/**
 * Created by wanglin  on 2018/1/10 17:18.
 */

public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements IView {
    protected P mPresenter;

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RxBus.get().register(this);
        if (rootView == null) {
            rootView = View.inflate(getActivity(), getLayoutId(), null);
            try {
                ButterKnife.bind(this, rootView);
            } catch (Exception e) {
                LogUtil.msg("--> 初始化失败 " + e.getMessage());
            }
            init();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName());

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (EmptyUtils.isNotEmpty(mPresenter)) {
            mPresenter.subscribe();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EmptyUtils.isNotEmpty(mPresenter)) {
            mPresenter.unsubscribe();
        }
        RxBus.get().unregister(this);
        Runtime.getRuntime().gc();
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    protected void onInvisible() {

    }

    protected void onVisible() {

    }
}
