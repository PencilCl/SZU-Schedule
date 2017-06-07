package cn.edu.szu.szuschedule.object;

import android.view.View;

/**
 * Created by chenlin on 07/06/2017.
 */
public class HomeworkItem {
    private String courseName; // 所属课程名
    private String name; // 作业名称
    private String deadline; //截止日期
    private Integer score; // 得分
    private OnClickListener onClickListener;

    public HomeworkItem(String courseName, String name, String deadline, OnClickListener onClickListener) {
        this(courseName, name, deadline, null, onClickListener);
    }

    public HomeworkItem(String courseName, String name, String deadline, Integer score, OnClickListener onClickListener) {
        this.courseName = courseName;
        this.name = name;
        this.deadline = deadline;
        this.score = score;
        this.onClickListener = onClickListener;
    }

    public String getCourseName() {
        return courseName;
    }

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

    public interface OnClickListener {
        void onClick(View view, HomeworkItem homeworkItem);
    }

}
