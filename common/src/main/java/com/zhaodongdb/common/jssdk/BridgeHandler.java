package com.zhaodongdb.common.jssdk;

/**
 *	Javascript调用的处理函数的接口
 */
public interface BridgeHandler {
	
	void handler(String data, CallBackFunction function);

}
