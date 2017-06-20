package cn.edu.szu.szuschedule.object;

import android.view.View;

/**
 * Created by chenlin on 07/06/2017.
 */
public class Homework {
    private BBCourseItem bbcourse; // 所属课程名
    private String name; // 作业名称
    private String deadline; //截止日期
    private Integer score; // 得分
    private OnClickListener onClickListener;
    private String description ;    //作业概括

    public Homework(BBCourseItem bbcourse, String name, String deadline, OnClickListener onClickListener, String description) {
        this(bbcourse, name, deadline, null, onClickListener,description);
    }
        //旧方法
    public Homework(String  bbcourse, String name, String deadline, Integer score, OnClickListener onClickListener, String description) {
        //this.bbcourse = bbcourse;
        this.name = name;
        this.deadline = deadline;
        this.score = score;
        this.onClickListener = onClickListener;
        this.description = description;
    }

    public Homework(BBCourseItem bbcourse, String name, String deadline, Integer score, OnClickListener onClickListener, String description) {
        this.bbcourse = bbcourse;
        this.name = name;
        this.deadline = deadline;
        this.score = score;
        this.onClickListener = onClickListener;
        this.description = description;
    }

    public BBCourseItem getBBcourse() {
        return bbcourse;
    }

    public String getCourseName()   {   return bbcourse.getSubjectName();}

    public String getName() {
        return name;
    }

    public String getDeadline() {
        return deadline;
    }

    public Integer getScore() {
        return score;
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public String getDescription()  { return description;   }

    public interface OnClickListener {
        void onClick(View view, Homework homeworkItem);
    }

}
