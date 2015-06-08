
package com.baidu.widgets.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class WebViewDemo extends Activity {

    WebView mWebView;
    CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.setDeviceDefaultLightTheme(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        mWebView = new WebView(this);
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mTitle = getTitle();

        mWebView.loadUrl("http://baike.baidu.com/");
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                setTitle("页面加载中....." + newProgress + "%");
                if(newProgress == 100) {
                    setTitle(mTitle);
                }
                super.onProgressChanged(view, newProgress);
            }

        });

        setContentView(mWebView);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
