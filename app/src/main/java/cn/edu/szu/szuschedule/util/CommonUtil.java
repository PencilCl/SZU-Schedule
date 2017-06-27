package cn.edu.szu.szuschedule.util;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by chenlin on 27/06/2017.
 */
public class CommonUtil {
    public static void initWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setLoadsImagesAutomatically(false); // 不显示图片
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 设置不使用缓存功能
        webView.setWebChromeClient(new WebChromeClient());
    }
}
