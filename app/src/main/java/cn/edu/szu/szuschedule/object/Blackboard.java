package cn.edu.szu.szuschedule.object;

/**
 * Created by Tongwen on 2017/6/20.
 */

public class Blackboard {
    private User user;
    private BBCourseItem bbCourseItem;

    public Blackboard(User user, BBCourseItem bbCourseItem){
        this.user = user;
        this.bbCourseItem = bbCourseItem;
    }

    public User getUser()   {   return user;    }

    public String getUserName()     {   return user.getStuNum();}

    public BBCourseItem getBBCourseItem()   {   return bbCourseItem;    }
}
