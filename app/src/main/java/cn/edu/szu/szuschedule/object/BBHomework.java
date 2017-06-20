package cn.edu.szu.szuschedule.object;

/**
 * Created by Tongwen on 2017/6/20.
 */

public class BBHomework {
    private Homework homework;
    private Attachment attachment;

    public BBHomework(Homework homework, Attachment attachment){
        this.homework = homework;
        this.attachment = attachment;
    }

    public Homework getHomework()   {return homework;}

    public Attachment getAttachment()   {   return attachment;}

    public String getHomeworkName() {   return homework.getName();  }
}
