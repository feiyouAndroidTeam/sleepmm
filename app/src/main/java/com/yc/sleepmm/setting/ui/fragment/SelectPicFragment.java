package com.yc.sleepmm.setting.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.vondear.rxtools.RxPhotoTool;
import com.yc.sleepmm.R;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by wanglin  on 2018/1/26 10:36.
 */

public class SelectPicFragment extends BottomSheetDialogFragment {


    private Context mContext;
    private View rootView;
    private BottomSheetDialog dialog;
    private BottomSheetBehavior<View> mBehavior;
    private TextView mTvTakeAlbum;
    private TextView mTvTakePhoto;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        //这里设置透明度
        windowParams.dimAmount = 0.5f;
        window.setAttributes(windowParams);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new BottomSheetDialog(getContext(), getTheme());
        if (rootView == null) {
            //缓存下来的 View 当为空时才需要初始化 并缓存
            rootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_select_pic, null);
            mTvTakeAlbum = rootView.findViewById(R.id.tv_take_album);
            mTvTakePhoto = rootView.findViewById(R.id.tv_take_photo);

        }
        dialog.setContentView(rootView);
        mBehavior = BottomSheetBehavior.from((View) rootView.getParent());
        ((View) rootView.getParent()).setBackgroundColor(Color.TRANSPARENT);
        rootView.post(new Runnable() {
            @Override
            public void run() {
                /**
                 * PeekHeight 默认高度 256dp 会在该高度上悬浮
                 * 设置等于 view 的高 就不会卡住
                 */
                mBehavior.setPeekHeight(rootView.getHeight());
            }
        });
        init();
        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除缓存 View 和当前 ViewGroup 的关联
        ((ViewGroup) (rootView.getParent())).removeView(rootView);
    }


    private void init() {
        RxView.clicks(mTvTakeAlbum).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                RxPhotoTool.openLocalImage(getActivity());
                dismiss();
            }
        });
        RxView.clicks(mTvTakePhoto).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                RxPhotoTool.openCameraImage(getActivity());

                dismiss();
            }
        });
    }


}
