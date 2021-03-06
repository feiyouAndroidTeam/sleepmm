package com.yc.sleepmm.vip.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding.view.RxView;
import com.kk.pay.I1PayAbs;
import com.kk.pay.IPayAbs;
import com.kk.pay.IPayCallback;
import com.kk.pay.OrderGood;
import com.kk.pay.OrderInfo;
import com.kk.pay.OrderParamsInfo;
import com.yc.sleepmm.R;
import com.yc.sleepmm.base.APP;
import com.yc.sleepmm.base.view.BaseActivity;
import com.yc.sleepmm.index.model.bean.UserInfo;
import com.yc.sleepmm.setting.constants.BusAction;
import com.yc.sleepmm.setting.constants.NetConstant;
import com.yc.sleepmm.setting.contract.VipContract;
import com.yc.sleepmm.setting.presenter.VipPresenter;
import com.yc.sleepmm.vip.ui.fragment.PaySuccessFragment;
import com.yc.sleepmm.vip.bean.GoodsInfo;
import com.yc.sleepmm.vip.bean.PayInfo;
import com.yc.sleepmm.vip.ui.adapter.VipPayAdapter;
import com.yc.sleepmm.vip.utils.GoodsInfoHelper;
import com.yc.sleepmm.vip.utils.PaywayHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;

/**
 * Created by wanglin  on 2018/1/25 11:37.
 */

public class VipActivity extends BaseActivity<VipPresenter> implements VipContract.View {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_vip_protocol)
    TextView tvVipProtocol;
    @BindView(R.id.recyclerView_good)
    RecyclerView recyclerViewGood;
    @BindView(R.id.recyclerView_pay)
    RecyclerView recyclerViewPay;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    @BindView(R.id.btn_charge)
    Button btnCharge;
    @BindView(R.id.ll_pay)
    LinearLayout llPay;
    @BindView(R.id.rl_good)
    RelativeLayout rlGood;
    @BindView(R.id.tv_vip_price)
    TextView tvVipPrice;
    //    private VipGoodAdapter vipGoodAdapter;
    private VipPayAdapter vipPayAdapter;
    private GoodsInfo goodsInfo;
    private PayInfo payInfo;
    private IPayAbs iPayAbs;


    @Override
    public int getLayoutId() {
        return R.layout.activity_vip;
    }

    @Override
    public void init() {
        mPresenter = new VipPresenter(this, this);
        tvTitle.setText(getString(R.string.vip_miemie));

        iPayAbs = new I1PayAbs(this);
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nestedScrollView.getLayoutParams();
//        layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT - ScreenUtil.dip2px(this, 50);
//        nestedScrollView.setLayoutParams(layoutParams);
        rlGood.setSelected(true);
//        recyclerViewGood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        vipGoodAdapter = new VipGoodAdapter(GoodsInfoHelper.getGoodsInfoList());
//
//        recyclerViewGood.setAdapter(vipGoodAdapter);
        if (GoodsInfoHelper.getGoodsInfoList() != null && GoodsInfoHelper.getGoodsInfoList().size() > 0) {
            goodsInfo = GoodsInfoHelper.getGoodsInfoList().get(0);
            tvVipPrice.setText(goodsInfo.vip_price);
        }
        recyclerViewPay.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        vipPayAdapter = new VipPayAdapter(PaywayHelper.getmPaywayInfo());
        recyclerViewPay.setAdapter(vipPayAdapter);
        if (PaywayHelper.getmPaywayInfo() != null && PaywayHelper.getmPaywayInfo().size() > 0) {
            payInfo = PaywayHelper.getmPaywayInfo().get(0);
        }
        setVip(APP.getInstance().getUserData());
        initListener();

    }

    private void initListener() {

        RxView.clicks(ivBack).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                finish();
            }
        });

        RxView.clicks(tvVipProtocol).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(VipActivity.this, VipProtocolActivity.class);
                startActivity(intent);
            }
        });
        RxView.clicks(btnCharge).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                UserInfo userInfo = APP.getInstance().getUserData();
                OrderParamsInfo orderParams = new OrderParamsInfo();
                orderParams.setPay_url(NetConstant.orders_init_url);
                orderParams.setUser_id(userInfo.getId());
                orderParams.setTitle(goodsInfo.name);
                orderParams.setMoney(goodsInfo.pay_price);
                if (payInfo != null) {
                    orderParams.setPay_way_name(payInfo.getPayway());
                }
                List<OrderGood> list = new ArrayList<>();
                OrderGood orderGood = new OrderGood(String.valueOf(goodsInfo.id), 1);
                list.add(orderGood);
                orderParams.setGoods_list(list);
                iPayAbs.pay(orderParams, new IPayCallback() {
                    @Override
                    public void onSuccess(OrderInfo orderInfo) {
                        mPresenter.getUserInfo(APP.getInstance().getUserData().getId());
                        PaySuccessFragment paySuccessFragment = new PaySuccessFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("orderSn", orderInfo.getOrder_sn());
                        paySuccessFragment.setArguments(bundle);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.add(paySuccessFragment, null);
                        ft.commitAllowingStateLoss();

//                        paySuccessFragment.setOnViewFinishListener(new PaySuccessFragment.onViewFinishListener() {
//                            @Override
//                            public void onViewFinish(PaySuccessFragment fragment) {
//                                if (fragment != null && fragment.isVisible())
//                                    fragment.dismiss();
//                                VipActivity.this.finish();
//                            }
//                        });

                    }

                    @Override
                    public void onFailure(OrderInfo orderInfo) {

                    }
                });

            }
        });

//        vipGoodAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                vipGoodAdapter.getView(position).setSelected(true);
//                goodsInfo = (GoodsInfo) adapter.getItem(position);
//            }
//        });

        vipPayAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                vipPayAdapter.getView(position).setSelected(true);
                payInfo = (PayInfo) adapter.getItem(position);
            }
        });
    }

    private void setVip(UserInfo userInfo) {
        if (userInfo.getVip() == 1) {
            llPay.setVisibility(View.GONE);
            btnCharge.setVisibility(View.GONE);
        } else {
            llPay.setVisibility(View.VISIBLE);
            btnCharge.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showUserInfo(UserInfo data) {
        setVip(data);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(BusAction.VIEW_FINISH)
            }
    )
    public void finishView(String tint) {
        finish();
    }
}
