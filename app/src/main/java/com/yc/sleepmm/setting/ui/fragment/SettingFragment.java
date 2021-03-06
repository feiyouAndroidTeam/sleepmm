package com.yc.sleepmm.setting.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding.view.RxView;
import com.kk.utils.ToastUtil;
import com.vondear.rxtools.RxPhotoTool;
import com.vondear.rxtools.view.dialog.RxDialogEditSureCancel;
import com.yc.sleepmm.R;
import com.yc.sleepmm.base.APP;
import com.yc.sleepmm.base.view.BaseActivity;
import com.yc.sleepmm.base.view.BaseFragment;
import com.yc.sleepmm.index.constants.Constant;
import com.yc.sleepmm.index.model.bean.UserInfo;
import com.yc.sleepmm.index.ui.activity.LoginGroupActivity;
import com.yc.sleepmm.setting.constants.BusAction;
import com.yc.sleepmm.setting.contract.SettingContract;
import com.yc.sleepmm.setting.presenter.SettingPresenter;
import com.yc.sleepmm.setting.ui.activity.FindCenterActivity;
import com.yc.sleepmm.setting.ui.activity.OptionFeedbackActivity;
import com.yc.sleepmm.setting.ui.activity.PresideIntroduceActivity;
import com.yc.sleepmm.setting.ui.activity.SkinActivity;
import com.yc.sleepmm.setting.ui.activity.SystemSettingActivity;
import com.yc.sleepmm.setting.ui.activity.UserKeepActivity;
import com.yc.sleepmm.setting.widget.BaseSettingView;
import com.yc.sleepmm.vip.ui.activity.VipActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;

/**
 * Created by wanglin  on 2018/1/10 17:18.
 */

public class SettingFragment extends BaseFragment<SettingPresenter> implements SettingContract.View {
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.baseSettingView_vip)
    BaseSettingView baseSettingViewVip;
    @BindView(R.id.baseSettingView_find)
    BaseSettingView baseSettingViewFind;
    @BindView(R.id.baseSettingView_person)
    BaseSettingView baseSettingViewPerson;
    @BindView(R.id.baseSettingView_alarm)
    BaseSettingView baseSettingViewAlarm;
    @BindView(R.id.baseSettingView_skin)
    BaseSettingView baseSettingViewSkin;
    @BindView(R.id.baseSettingView_feedback)
    BaseSettingView baseSettingViewFeedback;
    @BindView(R.id.baseSettingView_system)
    BaseSettingView baseSettingViewSystem;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.ll_login)
    LinearLayout llLogin;
    @BindView(R.id.tv_login_register)
    TextView mTvLoginRegister;
    @BindView(R.id.btn_vip)
    Button btnVip;
    @BindView(R.id.tv_expired_time)
    TextView tvExpiredTime;
    @BindView(R.id.baseSettingView_preside)
    BaseSettingView baseSettingViewPreside;

    private UserInfo userInfo;


    @Override
    public int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void init() {
        mPresenter = new SettingPresenter(getActivity(), this);
        mPresenter.getPayInfos();

        setUserInfo("");
        initListener();
    }


    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Constant.RX_LOGIN_SUCCESS)
            }
    )
    public void setUserInfo(String login) {
        userInfo = APP.getInstance().getUserData();
        if (userInfo == null) {
            llLogin.setVisibility(View.GONE);
            mTvLoginRegister.setVisibility(View.VISIBLE);
            roadImageView(R.mipmap.default_avatar, ivAvatar);
        } else {
            llLogin.setVisibility(View.VISIBLE);
            mTvLoginRegister.setVisibility(View.GONE);
            roadImageView(userInfo.getFace(), ivAvatar);
            tvName.setText(TextUtils.isEmpty(userInfo.getNick_name()) ? userInfo.getMobile() : userInfo.getNick_name());

            int vip = userInfo.getVip();

            btnVip.setText(vip == 0 ? getString(R.string.not_opened) : getString(R.string.opened));
            String vip_end_time = null;
            if (vip != 0 && !TextUtils.isEmpty(userInfo.getVip_end_time())) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String str = TimeUtils.date2String(new Date(Long.parseLong(userInfo.getVip_end_time()) * 1000), dateFormat);

                    if (str.equals(TimeUtils.date2String(new Date(), dateFormat))) {
                        str = getString(R.string.today);
                    }

                    vip_end_time = String.format(getString(R.string.expired_time), str);
                } catch (Exception e) {
                }
            }
            tvExpiredTime.setText(TextUtils.isEmpty(vip_end_time) ? getString(R.string.soon_open) : vip_end_time);

        }
    }

    private void initListener() {
        RxView.clicks(baseSettingViewVip).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (!APP.getInstance().isGotoLogin(getActivity())) {
                    Intent intent = new Intent(getActivity(), VipActivity.class);
                    startActivity(intent);
                }
            }
        });
        RxView.clicks(baseSettingViewFind).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(getActivity(), FindCenterActivity.class);
                startActivity(intent);
            }
        });
        RxView.clicks(baseSettingViewPerson).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (!APP.getInstance().isGotoLogin(getActivity())) {
                    Intent intent = new Intent(getActivity(), UserKeepActivity.class);
                    startActivity(intent);
                }
            }
        });
        RxView.clicks(baseSettingViewAlarm).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                AlarmFragment alarmFragment = new AlarmFragment();
                alarmFragment.show(getFragmentManager(), null);
            }
        });
        RxView.clicks(baseSettingViewSkin).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(getActivity(), SkinActivity.class);
                startActivity(intent);
            }
        });
        RxView.clicks(baseSettingViewFeedback).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (!APP.getInstance().isGotoLogin(getActivity())) {
                    Intent intent = new Intent(getActivity(), OptionFeedbackActivity.class);
                    startActivity(intent);
                }
            }
        });
        RxView.clicks(baseSettingViewSystem).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(getActivity(), SystemSettingActivity.class);
                startActivity(intent);
            }
        });

        RxView.clicks(ivAvatar).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (!APP.getInstance().isGotoLogin(getActivity())) {
                    SelectPicFragment selectPicFragment = new SelectPicFragment();
                    selectPicFragment.show(getFragmentManager(), null);
                }
            }
        });

        RxView.clicks(mTvLoginRegister).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(getActivity(), LoginGroupActivity.class);
                startActivity(intent);
            }
        });

        RxView.clicks(btnVip).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (userInfo.getVip() == 0) {
                    Intent intent = new Intent(getActivity(), VipActivity.class);
                    startActivity(intent);
                }
            }
        });


        RxView.clicks(tvName).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                final RxDialogEditSureCancel rxDialogEditSureCancel = new RxDialogEditSureCancel(getActivity());//提示弹窗

                rxDialogEditSureCancel.getTitleView().setText("请输入你的昵称");
                final EditText editText = rxDialogEditSureCancel.getEditText();
                editText.setHint(tvName.getText());
                rxDialogEditSureCancel.getSureView().setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String text = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(text)) {
                            ToastUtil.toast(getActivity(), "昵称不能为空");
                            return;
                        }
                        mPresenter.updateInfo(userInfo.getId(), text, "", "");
                        rxDialogEditSureCancel.cancel();
                    }
                });
                rxDialogEditSureCancel.getCancelView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rxDialogEditSureCancel.cancel();
                    }
                });
                rxDialogEditSureCancel.show();
            }
        });

        RxView.clicks(baseSettingViewPreside).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(getActivity(), PresideIntroduceActivity.class));
            }
        });


    }

    //从Uri中加载图片 并将其转化成File文件返回
    public void roadImageView(String path, ImageView imageView) {
        try {
            Glide.with(this).load(path).
                    apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).circleCrop()
                            .placeholder(R.mipmap.default_avatar)
                            .error(R.mipmap.default_avatar)
                            .fallback(R.mipmap.default_avatar)).
                    thumbnail(0.5f).
                    into(imageView);
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    //从Uri中加载图片 并将其转化成File文件返回
    public void roadImageView(int resId, ImageView imageView) {
        try {
            Glide.with(this).load(resId).
                    apply(new RequestOptions().circleCrop().error(R.mipmap.default_avatar)).into(imageView);
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(BusAction.GET_PICTURE)
            }
    )
    public void getPicture(Uri uri) {
        String path = RxPhotoTool.getImageAbsolutePath(getActivity(), uri);
        File file = new File(path);
        mPresenter.uploadFile(file, path.substring(path.lastIndexOf("/") + 1));
    }


    @Override
    public void showLoadingDialog(String mess) {
        ((BaseActivity) getActivity()).showLoadingDialog(mess);
    }

    @Override
    public void showLoadingProgressDialog(String mess, boolean isProgress) {

    }

    @Override
    public void dismissDialog() {
        ((BaseActivity) getActivity()).dismissDialog();
    }

    @Override
    public void dismissProgressDialog() {

    }

}
