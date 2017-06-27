package cn.edu.szu.szuschedule.util;

import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import io.reactivex.ObservableEmitter;

import java.util.regex.Pattern;

/**
 * Created by chenlin on 27/06/2017.
 * 处理统一身份认证的WebViewClient类
 */
public class SZUAuthenticationWebViewClient extends WebViewClient {
    private final static String loginJSCode = "document.getElementsByName(\"username\")[0].value = \"%s\";" +
            "document.getElementsByName(\"password\")[0].value = \"%s\";" +
            "document.getElementsByTagName(\"button\")[0].click();";

    private String username;
    private String password;
    private ObservableEmitter<String> e;
    private String loginPageUrl;
    private String targetUrl;

    public SZUAuthenticationWebViewClient(String username, String password, ObservableEmitter<String> e, String loginPageUrl, String targetUrl) {
        this.username = username;
        this.password = password;
        this.e = e;
        this.loginPageUrl = loginPageUrl;
        this.targetUrl = targetUrl;
    }

    @Override
    public void onPageFinished(final WebView view, String url) {
        super.onPageFinished(view, url);
        if (Pattern.compile(loginPageUrl).matcher(url).find()) {
            view.evaluateJavascript("document.getElementById(\"errorMsg\").innerText;", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    if ("null".equals(value)) {
                        view.evaluateJavascript(String.format(loginJSCode, username, password), null);
                    } else {
                        e.onError(new Throwable(value));
                    }
                }
            });
        } else if (targetUrl.equals(url)) {
            e.onNext("");
        }
    }
}