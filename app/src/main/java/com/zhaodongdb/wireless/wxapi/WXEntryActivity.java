package com.zhaodongdb.wireless.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhaodongdb.common.R;
import com.zhaodongdb.common.network.HttpRequestHelper;
import com.zhaodongdb.common.network.RequestUrlsEnum;
import com.zhaodongdb.common.network.Result;
import com.zhaodongdb.common.network.ZDHttpCallback;
import com.zhaodongdb.common.network.ZDHttpClient;
import com.zhaodongdb.common.network.ZDHttpFailure;
import com.zhaodongdb.common.network.ZDHttpResponse;
import com.zhaodongdb.common.router.ZDRouter;
import com.zhaodongdb.common.utils.BroadcastConstants;
import com.zhaodongdb.common.utils.Constants;
import com.zhaodongdb.common.utils.ThreadUtils;
import com.zhaodongdb.wireless.login.LoginRegisterRespData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	private static String TAG = "MicroMsg.WXEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, false);

        try {
            Intent intent = getIntent();
        	api.handleIntent(intent, this);
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
    	Log.i(TAG, req.toString());
        finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.i(TAG, resp.toString());
		int result = 0;
		
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		case BaseResp.ErrCode.ERR_UNSUPPORT:
			result = R.string.errcode_unsupported;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}

		if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
			SendAuth.Resp authResp = (SendAuth.Resp)resp;
			final String code = authResp.code;

			Map<String, String> body = new HashMap<>();
			body.put("weChatCode", code);
			ZDHttpClient.getInstance().asyncPost(
					RequestUrlsEnum.AUTH_BY_WE_CHAT.getEnvUrl(),
					HttpRequestHelper.buildJsonRequest(body),
					new ZDHttpCallback() {

						@Override
						public void onFailure(ZDHttpFailure failure) {
							Log.d(TAG, "wechat auth request error");
						}

						@Override
						public void onResponse(final ZDHttpResponse response) throws IOException {
							ThreadUtils.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									String resp = response.getResponseString();
									Result<LoginRegisterRespData> result = HttpRequestHelper.parseHttpResponse(resp, LoginRegisterRespData.class);
									if (result.isSuccess()) {
										if (TextUtils.isEmpty(result.getData().getVerifyUserId())) { // 首次使用微信登录，需要绑定手机号
											ZDRouter.navigation(String.format("/base/bindMobile?authChannel=%s&openId=%s", result.getData().getAuthChannel(), result.getData().getOpenId()));
										} else {
											if (result.getData().getHasSetGesture()) {
												ZDRouter.navigation(String.format("/base/patternlocker/checking?verifyUserId=%s", result.getData().getVerifyUserId()));
											} else {
												ZDRouter.navigation(String.format("/base/patternlocker/setting?verifyUserId=%s", result.getData().getVerifyUserId()));
											}
										}
										sendBroadcast(new Intent(BroadcastConstants.ZD_WECHAT_LOGIN_SUCCESS));
										WXEntryActivity.this.finish();
									} else {
										Toast.makeText(WXEntryActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
									}
								}
							});
						}
					});
		}

		Toast.makeText(this, getString(result) + ", type=" + resp.getType(), Toast.LENGTH_SHORT).show();

	}
}
