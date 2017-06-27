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

    /**
     * 将"周一"等字符串转换为对应数字
     * @param dayStr
     * @return
     */
    public static int getDay(String dayStr) {
        if (dayStr.endsWith("周一")) {
            return 1;
        }
        if (dayStr.endsWith("周二")) {
            return 2;
        }
        if (dayStr.endsWith("周三")) {
            return 3;
        }
        if (dayStr.endsWith("周四")) {
            return 4;
        }
        if (dayStr.endsWith("周五")) {
            return 5;
        }
        return 0;
    }

}
