package cn.edu.szu.szuschedule.object;

/**
 * Created by chenlin on 01/06/2017.
 */
public class ModuleItem {
    private int moduleNameRes;
    private int bgRes;

    public ModuleItem(int moduleNameRes, int bgRes) {
        this.moduleNameRes = moduleNameRes;
        this.bgRes = bgRes;
    }

    public int getModuleNameRes() {
        return moduleNameRes;
    }

    public int getBgRes() {
        return bgRes;
    }
}
