//notation: js file can only use this kind of comments
//since comments will cause error when use in webview.loadurl,
//comments will be remove by java use regexp
(function() {
    console.log("WebViewJavascriptBridge start");
    if (window.WebViewJavascriptBridge) {
        return;
    }

    var messagingIframe;
    var messageHandlers = {};

    var serialNumber = 0;
    var JSSDK_SCHEMA = "zhaodong";
    var JSSDK_HOST = "jssdk";
    var callbackMap = {};

    function _createQueueReadyIframe(doc) {
        messagingIframe = doc.createElement('iframe');
        messagingIframe.style.display = 'none';
        doc.documentElement.appendChild(messagingIframe);
    }

    function init(messageHandler) {
        if (WebViewJavascriptBridge._messageHandler) {
            throw new Error('WebViewJavascriptBridge.init called twice');
        }
        WebViewJavascriptBridge._messageHandler = messageHandler;
    }

    function registerHandler(handlerName, handler) {
        messageHandlers[handlerName] = handler;
    }

    function unregisterHandler(handlerName, handler) {
        delete messageHandlers[handlerName];
    }

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

    function handleCallFromNative(method, data, callbackId) {
        setTimeout(function () {
            if (callbackId) {
                callback = function(data) {
                    callbackToNative(callbackId, data);
                };
            }
            var handler = WebViewJavascriptBridge._messageHandler;
            if (method) {
                handler = messageHandlers[method];
            }
            try {
                handler(data, callback);
            } catch (exception) {
                if (typeof console != 'undefined') {
                    console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", message, exception);
                }
            }
        });
    }

    function callbackToNative(callbackId, data) {
        var param = encodeURIComponent(JSON.stringify(data));
        messagingIframe.src = JSSDK_SCHEMA + "://" + JSSDK_HOST + "/onCallbackFromJavascript" + "?callbackid=" + callbackId + "&param=" + param;
    }
    
    function onCallbackFromNative(callbackId, data) {
        if (!callbackId) {
            return;
        }
        var callback = callbackMap[callbackId];
        if (!callback) {
            return;
        }
        callback(data);
        delete callbackMap[callbackId];
    }

    var WebViewJavascriptBridge = window.WebViewJavascriptBridge = {
        init: init,
        registerHandler: registerHandler,
        unregisterHandler: unregisterHandler,
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
    console.log("WebViewJavascriptBridge loaded");
})();
