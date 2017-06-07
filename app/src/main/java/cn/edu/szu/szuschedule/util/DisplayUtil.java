package cn.edu.szu.szuschedule.util;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by chenlin on 07/06/2017.
 */
public class DisplayUtil {

    public static void setTranslucentStatus(Activity activity) {
        Window window = activity.getWindow();
        //设置透明状态栏,这样才能让 ContentView 向上
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }
}

