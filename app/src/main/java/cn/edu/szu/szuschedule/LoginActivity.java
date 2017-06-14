package cn.edu.szu.szuschedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.edu.szu.szuschedule.service.BBService;
import cn.edu.szu.szuschedule.util.LoadingUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

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
    }

    @Override
    public void onClick(View v) {
        loadingUtil.showLoading();
        BBService.loginBB(stuNum.getText().toString(), password.getText().toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String stuNum) throws Exception {
                        loadingUtil.hideLoading();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
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
