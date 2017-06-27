package cn.edu.szu.szuschedule.object;

import android.view.View;

/**
 * Created by Tongwen on 2017/6/20.
 */

public class BBCourseItem {
    private  int id;
    private String subjectName;
    private BBHomework homeworkItem;
    private OnClickListener onClickListener;
    private String termNum;
    private String courseNum;

    public BBCourseItem(int id,String sub,String courseNum,String termNum, BBHomework home, OnClickListener onClickListener){
        this.id = id;
        this.subjectName = sub;
        this.homeworkItem = home;
        this.courseNum = courseNum;
        this.termNum = termNum;
        this.onClickListener = onClickListener;
    }

    public String getSubjectName(){ return subjectName;}

    public BBHomework getHomeworkItem()   {   return homeworkItem;}

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public interface OnClickListener {
        void onClick(View view, BBCourseItem bbCourseItem);
    }

    public String getTermNum()  { return termNum;}

    public String getCourseNum()    {   return courseNum;}

    public void setId(int dd){  id = dd;  }

    public int getId()  { return  id;}
}
