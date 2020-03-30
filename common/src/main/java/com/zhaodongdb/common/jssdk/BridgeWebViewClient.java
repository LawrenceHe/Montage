package com.zhaodongdb.common.jssdk;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URI;
import java.net.URISyntaxException;


public class BridgeWebViewClient extends WebViewClient {
    private BridgeWebView webView;

    public BridgeWebViewClient(BridgeWebView webView) {
        this.webView = webView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        try {
            URI uri = new URI(url);
            if (BridgeUtil.JSSDK_SCHEMA.equals(uri.getScheme()) && BridgeUtil.JSSDK_HOST.equals(uri.getHost())) {
                webView.handleCallFromJavascript(uri);
                return true;
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        BridgeUtil.webViewLoadLocalJs(view, BridgeWebView.toLoadJs);

        if (webView.getStartupMessage() != null) {
            for (Message m : webView.getStartupMessage()) {
                webView.dispatchMessage2Javascript(m);
            }
            webView.setStartupMessage(null);
        }
    }
}
