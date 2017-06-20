package cn.edu.szu.szuschedule.object;

import android.view.View;

/**
 * Created by jazzyzhong on 2017/6/7..
 * 待删
 */
public class SubjectItem {
    private String subname;
    private String substatus;
    private OnClickListener onClickListener;

    public SubjectItem(String name,String status, OnClickListener onClickListener){
        this.subname = name;
        this.substatus = status;
        this.onClickListener = onClickListener;
    }
    public String getSubname(){return this.subname;}
    public String getSubstatus(){return this.substatus;}

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public interface OnClickListener {
        void onClick(View view, SubjectItem subjectItem);
    }
}
