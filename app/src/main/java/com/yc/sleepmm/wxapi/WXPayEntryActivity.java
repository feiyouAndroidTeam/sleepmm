package com.yc.sleepmm.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.kk.pay.IPayImpl;
import com.kk.utils.LogUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by wanglin  on 2018/2/25 10:07.
 */

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, IPayImpl.appid); //appid需换成商户自己开放平台appid
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {

        LogUtil.msg("TAG: " + Thread.currentThread().getName());

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX && IPayImpl.uOrderInfo != null && IPayImpl.uiPayCallback != null) {
            // resp.errCode == -1 原因：支付错误,可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
            // resp.errCode == -2 原因 用户取消,无需处理。发生场景：用户不支付了，点击取消，返回APP
            if (resp.errCode == 0) //支付成功
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showLong("支付成功");
                        IPayImpl.uOrderInfo.setMessage("支付成功");
                        IPayImpl.uiPayCallback.onSuccess(IPayImpl.uOrderInfo);
                    }
                });

            } else if (resp.errCode == -1) // 支付错误
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showLong("支付错误");
                        IPayImpl.uOrderInfo.setMessage("支付错误");
                        IPayImpl.uiPayCallback.onFailure(IPayImpl.uOrderInfo);
                    }
                });


            } else if (resp.errCode == -2) // 支付取消
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showLong("支付取消");
                        IPayImpl.uOrderInfo.setMessage("支付取消");
                        IPayImpl.uiPayCallback.onFailure(IPayImpl.uOrderInfo);
                    }
                });


            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showLong("支付失败");
                        IPayImpl.uOrderInfo.setMessage("支付失败");
                        IPayImpl.uiPayCallback.onFailure(IPayImpl.uOrderInfo);
                    }
                });

            }
            IPayImpl.uOrderInfo = null;
            IPayImpl.uiPayCallback = null;
            finish();
        }
    }
}
