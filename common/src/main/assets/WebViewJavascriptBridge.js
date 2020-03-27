//notation: js file can only use this kind of comments
//since comments will cause error when use in webview.loadurl,
//comments will be remove by java use regexp
(function() {
    if (window.WebViewJavascriptBridge) {
        return;
    }

    var messagingIframe;        // iframe控件
    var messageHandlers = {};   // native的调用处理函数映射表

    var serialNumber = 0;
    var JSSDK_SCHEMA = "zhaodong";
    var JSSDK_HOST = "jssdk";
    var callbackMap = {};       // 回调函数表

    // 创建消息index队列iframe，通过iframe发送GET请求，实现对Native的通信
    function _createQueueReadyIframe(doc) {
        messagingIframe = doc.createElement('iframe');
        messagingIframe.style.display = 'none';
        doc.documentElement.appendChild(messagingIframe);
    }
    
    //set default messageHandler  初始化默认的消息处理器
    function init(messageHandler) {
        if (WebViewJavascriptBridge._messageHandler) {
            throw new Error('WebViewJavascriptBridge.init called twice');
        }
        WebViewJavascriptBridge._messageHandler = messageHandler;
    }

    // 注册来自Native层的消息处理函数
    function registerHandler(handlerName, handler) {
        messageHandlers[handlerName] = handler;
    }

    // 调用Native方法
    function callNativeFunction(method, data, callback) {
        if (data) {
            var param = encodeURIComponent(JSON.stringify(data));
        } else {
            var param = "";
        }

        if (callback) {
            var callbackId = 'javascript_cbid_' + (serialNumber++) + '_' + new Date().getTime();
            callbackMap[callbackId] = callback;
        } else {
            var callbackId = "";
        }

        messagingIframe.src = JSSDK_SCHEMA + "://" + JSSDK_HOST + "/" + method + "?callbackid=" + callbackId + "&param=" + param;
    }

    // 处理来自Native的调用
    function handleCallFromNative(method, data, callbackId) {
        setTimeout(function () {
            //生成回调函数
            if (callbackId) {
                callback = function(data) {
                    callbackToNative(callbackId, data);
                };
            }
            // 找到处理函数
            var handler = WebViewJavascriptBridge._messageHandler;
            if (method) {
                handler = messageHandlers[method];
            }
            // 执行处理函数
            try {
                handler(data, callback);
            } catch (exception) {
                if (typeof console != 'undefined') {
                    console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", method, data, exception);
                }
            }
        });
    }

    // 回调Native
    function callbackToNative(callbackId, data) {
        var param = encodeURIComponent(JSON.stringify(data));
        messagingIframe.src = JSSDK_SCHEMA + "://" + JSSDK_HOST + "/onCallbackFromJavascript" + "?callbackid=" + callbackId + "&param=" + param;
    }

    // 处理Native调用
    function onCallbackFromNative(callbackId, data) {
        var callback = callbackMap[callbackId];
        if (!callback) {
            return;
        }

        callback(data);
    }

    var WebViewJavascriptBridge = window.WebViewJavascriptBridge = {
        init: init,
        registerHandler: registerHandler,
        handleCallFromNative: handleCallFromNative,
        onCallbackFromNative: onCallbackFromNative,
        callNativeFunction: callNativeFunction
    };

    var doc = document;
    _createQueueReadyIframe(doc);
    var readyEvent = doc.createEvent('Events');
    readyEvent.initEvent('WebViewJavascriptBridgeReady');
    readyEvent.bridge = WebViewJavascriptBridge;
    doc.dispatchEvent(readyEvent);
})();
