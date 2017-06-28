package cn.edu.szu.szuschedule;

import android.app.Application;
import cn.edu.szu.szuschedule.service.BBService;
import cn.edu.szu.szuschedule.service.LibraryService;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import okhttp3.OkHttpClient;


/**
 * Created by chenlin on 12/06/2017.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initOkGo();
        BBService.init(this);
        LibraryService.getInstance()
                .init(this);
    }

    private void initOkGo() {
        HttpsUtils.SSLParams sslParams1;
        sslParams1 = HttpsUtils.getSslSocketFactory();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        OkGo.getInstance()
                .init(this)
                .setOkHttpClient(builder.build());
    }
}
