package com.zhaodongdb.common.jssdk;

/**
 * 用来处理在WebViewJavascriptBridge.js基础库被加载之前，Native向Javascript发起的调用
 * 此时先把相关调用参数放在消息队列中，等JS初始化完毕之后再统一调用
 */
public class Message {

	private String callbackId; //callbackId
	private String data; //data of message
	private String handlerName; //name of handler

	public String getCallbackId() {
		return callbackId;
	}
	public void setCallbackId(String callbackId) {
		this.callbackId = callbackId;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getHandlerName() {
		return handlerName;
	}
	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

}
