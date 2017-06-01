package cn.edu.szu.szuschedule.object;

import android.view.View;

/**
 * Created by chenlin on 01/06/2017.
 */
public class ModuleItem {
    private int moduleNameRes;
    private int bgRes;
    View.OnClickListener onClickListener;

    public ModuleItem(int moduleNameRes, int bgRes, View.OnClickListener onClickListener) {
        this.moduleNameRes = moduleNameRes;
        this.bgRes = bgRes;
        this.onClickListener = onClickListener;
    }

    public int getModuleNameRes() {
        return moduleNameRes;
    }

    public int getBgRes() {
        return bgRes;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }
}
