package cn.edu.szu.szuschedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.edu.szu.szuschedule.object.User;
import cn.edu.szu.szuschedule.service.BBService;
import cn.edu.szu.szuschedule.service.UserService;
import cn.edu.szu.szuschedule.util.LoadingUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by chenlin on 24/05/2017.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.loginButton) Button loginButton;
    @Bind(R.id.stuNum) EditText stuNum;
    @Bind(R.id.password) EditText password;
    LoadingUtil loadingUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loadingUtil = new LoadingUtil(this);

        init();
    }

    private void init() {
        loginButton.setOnClickListener(this);

        // 判断用户是否已登录
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        if (sp.getInt("id", -1) != -1) {
            UserService.setCurrentUser(new User(sp.getInt("id", -1),
                    sp.getString("account", ""),
                    sp.getString("password", ""),
                    sp.getString("sex", "无"),
                    sp.getString("stuNum", ""),
                    sp.getString("name", "")));

            BBService.initData();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        // 检查输入信息
        final String account = stuNum.getText().toString();
        final String passwordStr = password.getText().toString();
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(passwordStr)) {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return ;
        }

        // 登录操作
        loadingUtil.showLoading();
        BBService.loginBB(account, passwordStr)
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        return BBService.getStuNum();
                    }
                })
                .flatMap(new Function<String, ObservableSource<User>>() {
                    @Override
                    public ObservableSource<User> apply(String stuNum) throws Exception {
                        return UserService.getUserInfo(new User(-1, account, passwordStr, stuNum));
                    }
                })
                .flatMap(new Function<User, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(User user) throws Exception {
                        if (user == null) {
                            return Observable.create(new ObservableOnSubscribe<Boolean>() {
                                @Override
                                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                                    e.onError(new Throwable("获取信息失败"));
                                }
                            });
                        }
                        // 保存当前用户信息
                        return UserService.saveUserInfo(LoginActivity.this, user);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            loadingUtil.hideLoading();
                            BBService.initData();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "保存用户信息失败，尝试从重新登录", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        loadingUtil.hideLoading();
                        Toast.makeText(LoginActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
