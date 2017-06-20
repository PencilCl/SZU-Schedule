package cn.edu.szu.szuschedule.object;

import android.view.View;

/**
 * Created by Tongwen on 2017/6/20.
 */

public class BBCourseItem {
    private String subjectName;
    private Homework homeworkItem;
    private OnClickListener onClickListener;


    public BBCourseItem(String sub, Homework home, OnClickListener onClickListener){
        this.subjectName = sub;
        this.homeworkItem = home;
        this.onClickListener = onClickListener;
    }

    public String getSubjectName(){ return subjectName;}

    public Homework getHomeworkItem()   {   return homeworkItem;}

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public interface OnClickListener {
        void onClick(View view, BBCourseItem bbCourseItem);
    }
}
