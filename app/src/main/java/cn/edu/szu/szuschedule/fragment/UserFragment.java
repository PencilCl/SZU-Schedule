package cn.edu.szu.szuschedule.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;
import cn.edu.szu.szuschedule.LoginActivity;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.UserTellUsActivity;

import android.widget.Button;
import cn.edu.szu.szuschedule.util.LoadingUtil;
import com.lzy.okgo.OkGo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by chenlin on 24/05/2017.
 */
public class UserFragment extends Fragment{
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, null);
        Button loginOut = (Button)view.findViewById(R.id.loginOut);
        loginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearWebViewCache();
                clearSharedPreferences();
                getActivity().startActivity(new Intent(getActivity(),LoginActivity.class));
                getActivity().finish();
            }
        });
        Button aboutUs =  (Button)view.findViewById(R.id.aboutUs);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getContext(),UserTellUsActivity.class));
            }
        });
        return view;
    }

    /**
     * 清除SharedPreferences下当前用户的信息
     */
    private void clearSharedPreferences() {
        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("id");
        editor.remove("account");
        editor.remove("password");
        editor.remove("name");
        editor.remove("sex");
        editor.remove("stuNum");
        editor.commit();
    }

    public void clearWebViewCache() {
        // 清除cookie即可彻底清除缓存
        CookieSyncManager.createInstance(getContext());
        CookieManager.getInstance().removeAllCookie();
    }
}
