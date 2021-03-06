package com.yc.sleepmm.index.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.yc.sleepmm.index.model.bean.MusicTypeInfo;
import com.yc.sleepmm.index.ui.fragment.HomeMusicListFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * ragment适配器
 * TinyHung@Outlook.com
 * 2017/1/18.
 */

public class AppFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private List<HomeMusicListFragment> mFragment;
    private List<MusicTypeInfo> mTitleList;

    /**
     * 普通，主页使用
     */
    public AppFragmentPagerAdapter(FragmentManager fm, List<HomeMusicListFragment> mFragment) {
        super(fm);
        this.mFragment = mFragment;
    }

    /**
     * 接收首页传递的标题
     */
    public AppFragmentPagerAdapter(FragmentManager fm, List<HomeMusicListFragment> mFragment, List<MusicTypeInfo> mTitleList) {
        super(fm);
        this.mFragment = mFragment;
        this.mTitleList = mTitleList;
    }

    @Override
    public Fragment getItem(int position) {
        return null == mFragment ? null : (Fragment) mFragment.get(position);
    }

    @Override
    public int getCount() {
        return null == mFragment ? 0 : mFragment.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    /**
     * 首页显示title，每日推荐等..
     * 若有问题，移到对应单独页面
     */
    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitleList != null && mTitleList.size() > 0 && position < mTitleList.size()) {
            return mTitleList.get(position).title;
        } else {
            return "";
        }
    }

    /**
     * 添加单个Fragment
     *
     * @param fragment
     * @param title
     */
    public void addFragment(HomeMusicListFragment fragment, String title) {
        if (null == mFragment) {
            mFragment = new ArrayList<>();
        }
        mFragment.add(0, fragment);
    }

    /**
     * 设置全新的Fragment
     *
     * @param fragment
     */
    public void setNewFragments(List<HomeMusicListFragment> fragment, List<MusicTypeInfo> titleList) {
        if (null != mFragment) mFragment.clear();
        this.mFragment = fragment;
        if (null != mTitleList) mTitleList.clear();
        this.mTitleList = titleList;
        notifyDataSetChanged();
    }

    /**
     * 追加多个Fragmnet
     */
    public void addFragments(List<HomeMusicListFragment> fragments, List<MusicTypeInfo> titles) {
        for (HomeMusicListFragment fragment : fragments) {
            mFragment.add(fragment);
        }
        for (MusicTypeInfo title : titles) {
            mTitleList.add(title);
        }
        notifyDataSetChanged();
    }

}
