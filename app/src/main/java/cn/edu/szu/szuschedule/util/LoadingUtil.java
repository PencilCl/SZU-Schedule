package cn.edu.szu.szuschedule.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import cn.edu.szu.szuschedule.R;

/**
 * Created by chenlin on 14/06/2017.
 * 加载界面统一管理工具
 */
public class LoadingUtil implements DialogInterface.OnCancelListener {
    private Dialog mDialog;
    private Integer count; // 计数当前需要显示Loading界面

    public LoadingUtil(Context context) {
        count = 0;

        // 初始化Dialog
        View mView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_loading, null);
        mDialog = new Dialog(context, R.style.LoadingDialogStyle);
        mDialog.setContentView(mView);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnCancelListener(this);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        hideLoading();
    }

    public void showLoading() {
        synchronized (count) {
            ++count;
            if (count == 1) {
                mDialog.show();
            }
        }
    }

    public void hideLoading() {
        synchronized (count) {
            if (count > 0) {
                --count;
                if (count == 0 && mDialog != null) {
                    mDialog.dismiss();
                }
            }
        }
    }
}
