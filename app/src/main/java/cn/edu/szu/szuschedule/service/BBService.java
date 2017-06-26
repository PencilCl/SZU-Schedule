package cn.edu.szu.szuschedule.service;

import android.app.Application;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lzy.okgo.OkGo;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lzy.okgo.convert.StringConvert;
import com.lzy.okrx2.adapter.ObservableBody;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by chenlin on 14/06/2017.
 * 使用BBService前必须先调用init方法
 */
public class BBService {
    private final static String baseUrl = "http://elearning.szu.edu.cn";
    private final static String loginPageUrl = "https://authserver.szu.edu.cn/authserver/login(.*?)?service=https%3a%2f%2fauth.szu.edu.cn%2fcas.aspx%2flogin%3fservice%3dhttp%3a%2f%2felearning.szu.edu.cn%2fwebapps%2fcbb-sdgxtyM-BBLEARN%2fgetuserid.jsp";
    private final static String enterBBUrl = "http://elearning.szu.edu.cn/webapps/cbb-sdgxtyM-BBLEARN/checksession.jsp";
    private final static String loginJSCode = "document.getElementsByName(\"username\")[0].value = \"%s\";" +
            "document.getElementsByName(\"password\")[0].value = \"%s\";" +
            "document.getElementsByTagName(\"button\")[0].click();";
    private final static String bbUrl = "http://elearning.szu.edu.cn/webapps/portal/frameset.jsp"; // 成功进入bb的页面
    private final static String booksUrl = "http://opac.lib.szu.edu.cn/opac/user/bookborrowed.aspx";
    private final static String stuNumUrl = "http://elearning.szu.edu.cn/webapps/blackboard/execute/editUser?context=self_modify"; // 获取学号url

    private final static String stuNumReg = "<input.*id=\"studentId\".*value=\"(.*?)\".*/>";
    private static Pattern stuNumPattern;

    private WebView webView;

    private BBService() {
        stuNumPattern = Pattern.compile(stuNumReg);
    }

    public static BBService getInstance() {
        return BBServiceHolder.holder;
    }

    private static class BBServiceHolder {
        private static BBService holder = new BBService();
    }

    /**
     * 初始化BBService
     */
    public void init(Application app) {
        // 初始化WebView
        this.webView = new WebView(app);
        WebSettings settings = webView.getSettings();
        settings.setLoadsImagesAutomatically(false); // 不显示图片
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 设置不使用缓存功能
        webView.setWebChromeClient(new WebChromeClient());
    }

    /**
     * 登录bb
     * 使用bb相关功能前应当调用此方法，确保完成了bb的登录
     * @param username 校园卡号
     * @param password 统一身份认证密码
     * @return 返回Observable对象，返回数据为登录用户学号
     */
    public static Observable<String> loginBB(final String username, final String password) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                BBService bbService = getInstance();
                bbService.webView.setWebViewClient(bbService.new MyWebViewClient(username, password, e));
                bbService.webView.loadUrl(enterBBUrl);
            }
        }).flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(String s) throws Exception {
                return getStuNum();
            }
        });
    }

    /**
     * 获取当前登录用户的学号
     * @return 成功返回学号，失败返回空字符
     */
    public static Observable<String> getStuNum() {
        CookieManager cookieManager = CookieManager.getInstance();
        return OkGo.<String>get(stuNumUrl)
                .headers("Cookie", cookieManager.getCookie(baseUrl))
                .converter(new StringConvert())
                .adapt(new ObservableBody<String>())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        Matcher matcher = stuNumPattern.matcher(s);
                        if (matcher.find()) {
                            return matcher.group(1);
                        } else {
                            return "";
                        }
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    /**
     * 登录图书馆
     * 登录BB成功后调用
     */
    public static void loginLibrary() {
        CookieManager cookieManager = CookieManager.getInstance();
        if(cookieManager.getCookie(booksUrl) == null) {
            getInstance().webView.loadUrl(booksUrl);
        }
    }

    class MyWebViewClient extends WebViewClient {
        private String username;
        private String password;
        private ObservableEmitter<String> e;

        public MyWebViewClient(String username, String password, ObservableEmitter<String> e) {
            this.username = username;
            this.password = password;
            this.e = e;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            System.out.println(url);
            if (Pattern.compile(loginPageUrl).matcher(url).find()) {
                view.evaluateJavascript("document.getElementById(\"errorMsg\").innerText;", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        if ("null".equals(value)) {
                            webView.evaluateJavascript(String.format(loginJSCode, username, password), null);
                        } else {
                            e.onError(new Throwable(value));
                        }
                    }
                });
            } else if (bbUrl.equals(url)) {
                e.onNext("");
            }
        }
    }
}
