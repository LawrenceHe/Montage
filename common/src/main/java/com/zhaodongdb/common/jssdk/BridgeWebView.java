package com.zhaodongdb.common.jssdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebView;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("SetJavaScriptEnabled")
public class BridgeWebView extends WebView implements WebViewJavascriptBridge{

	private final String TAG = "BridgeWebView";

	private final int URL_MAX_CHARACTER_NUM = 2097152;
	public static final String toLoadJs = "WebViewJavascriptBridge.js";
	Map<String, CallBackFunction> callbackMap = new HashMap<String, CallBackFunction>();
	Map<String, BridgeHandler> messageHandlers = new HashMap<String, BridgeHandler>();
	BridgeHandler defaultHandler = new DefaultHandler();

	private List<Message> startupMessage = new ArrayList<Message>();

	public List<Message> getStartupMessage() {
		return startupMessage;
	}

	public void setStartupMessage(List<Message> startupMessage) {
		this.startupMessage = startupMessage;
	}

	private long uniqueId = 0;

	public BridgeWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BridgeWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public BridgeWebView(Context context) {
		super(context);
		init();
	}

	/**
	 *
	 * @param handler
	 *            default handler,handle messages send by js without assigned handler name,
     *            if js message has handler name, it will be handled by named handlers registered by native
	 */
	public void setDefaultHandler(BridgeHandler handler) {
       this.defaultHandler = handler;
	}

    private void init() {
		this.setVerticalScrollBarEnabled(false);
		this.setHorizontalScrollBarEnabled(false);
		this.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
		this.setWebViewClient(generateBridgeWebViewClient());
	}

    protected BridgeWebViewClient generateBridgeWebViewClient() {
        return new BridgeWebViewClient(this);
    }


	@Override
	public void send(String data) {
		send(data, null);
	}

	@Override
	public void send(String data, CallBackFunction responseCallback) {
		doSend(null, data, responseCallback);
	}

    /**
     * 保存message到消息队列
     * @param handlerName handlerName
     * @param data data
     * @param responseCallback CallBackFunction
     */
	private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
		Message m = new Message();
		if (!TextUtils.isEmpty(data)) {
			m.setData(data);
		}
		if (responseCallback != null) {
			String callbackId = String.format(BridgeUtil.CALLBACK_ID_FORMAT, ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
			callbackMap.put(callbackId, responseCallback);
			m.setCallbackId(callbackId);
		}
		if (!TextUtils.isEmpty(handlerName)) {
			m.setHandlerName(handlerName);
		}
		queueMessage2Javascript(m);
	}

	/**
	 * register handler,so that javascript can call it
	 * 注册处理程序,以便javascript调用它
	 * @param handlerName handlerName
	 * @param handler BridgeHandler
	 */
	public void registerHandler(String handlerName, BridgeHandler handler) {
		if (!TextUtils.isEmpty(handlerName) && handler != null) {
            // 添加至 Map<String, BridgeHandler>
			messageHandlers.put(handlerName, handler);
		}
	}

	/**
	 * unregister handler
	 *
	 * @param handlerName
	 */
	public void unregisterHandler(String handlerName) {
		if (!TextUtils.isEmpty(handlerName)) {
			messageHandlers.remove(handlerName);
		}
	}

	/**
	 * call javascript registered handler
	 * 调用javascript处理程序注册
     * @param handlerName handlerName
	 * @param data data
	 * @param callBack CallBackFunction
	 */
	public void callJavascriptFunction(String handlerName, String data, CallBackFunction callBack) {
        doSend(handlerName, data, callBack);
	}

	public void onCallbackFromJavascript(String callbackId, String data) {
		if (!TextUtils.isEmpty(callbackId)) {
			CallBackFunction callBackFunction = callbackMap.get(callbackId);
			callBackFunction.onCallBack(data);
		}
	}

	public void handleCallFromJavascript(URI uri) {
		// 解析URL中相关参数
		String method = uri.getPath().replace("/", "");
		String[] query = uri.getQuery().split("&");
		String param = "";
		String callbackId = "";
		for (String q : query) {
			if (q.startsWith("param=")) {
				param = q.replace("param=", "");
			} else if (q.startsWith("callbackid=")) {
				callbackId = q.replace("callbackid=", "");
			}
		}

		if ("onCallbackFromJavascript".equals(method)) {
			onCallbackFromJavascript(callbackId, param);
		}

		// 创建回调函数
		CallBackFunction callbackFunction = null;
		// if had callbackId 如果有回调Id
		final String cbid = callbackId;
		if (!TextUtils.isEmpty(callbackId)) {
			callbackFunction = new CallBackFunction() {
				@Override
				public void onCallBack(String data) {
					callbackToJavascript(cbid, data);
				}
			};
		} else {
			callbackFunction = new CallBackFunction() {
				@Override
				public void onCallBack(String data) {
					// do nothing
				}
			};
		}
		// 找到对应的处理函数并执行，若找不到则使用默认的处理函数
		BridgeHandler handler;
		if (!TextUtils.isEmpty(method)) {
			handler = messageHandlers.get(method);
		} else {
			handler = defaultHandler;
		}
		if (handler != null){
			handler.handler(param, callbackFunction);
		}
	}

	public void callbackToJavascript(final String callbackId, final String data) {
		// 必须要找主线程才会将数据传递出去
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				String javascriptCommand = String.format(BridgeUtil.ON_CALLBACK_FROM_NATIVE, callbackId, data);
				evaluateJavascript(javascriptCommand, null);
			}
		});
	}

	/**
	 * list<message> != null 添加到消息集合否则分发消息
	 * @param m Message
	 */
	private void queueMessage2Javascript(Message m) {
		if (startupMessage != null) {
			startupMessage.add(m);
		} else {
			dispatchMessage2Javascript(m);
		}
	}

	/**
	 * 分发message 必须在主线程才分发成功
	 * @param m Message
	 */
	void dispatchMessage2Javascript(final Message m) {

		// 必须要找主线程才会将数据传递出去
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				String javascriptCommand = String.format(BridgeUtil.HANDLE_CALL_FROM_NATIVE, m.getHandlerName(), m.getData(), m.getCallbackId());
				evaluateJavascript(javascriptCommand, null);
			}
		});

	}

}
