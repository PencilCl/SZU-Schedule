package cn.edu.szu.szuschedule.object;

/**
 * Created by Tongwen on 2017/6/20.
 */

public class ScheduleItem {
    private User user;
    private Course course ;

    ScheduleItem(   User user, Course course){
        this.user = user;
        this.course = course;
    }

    public User getUser ()  {  return user; }

    public String getUserName() { return user.getName();    }

    public Course getCourse()   { return course;}
}
