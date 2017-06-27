package cn.edu.szu.szuschedule.object;

/**
 * Created by jazzyzhong on 2017/6/7..
 * 待删
 */
public class SubjectItem {
    private int id;
    private String subjectName;
    private String courseId;
    private String termNum;
    private String courseNum;
    private String status;

    public SubjectItem(int id, String subjectName, String courseId, String courseNum, String termNum){
        this(id, subjectName, courseId, courseNum, termNum, null);
    }

    public SubjectItem(int id, String subjectName, String courseId, String courseNum, String termNum, String status){
        this.id = id;
        this.subjectName = subjectName;
        this.courseId = courseId;
        this.courseNum = courseNum;
        this.termNum = termNum;
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setTermNum(String termNum) {
        this.termNum = termNum;
    }

    public void setCourseNum(String courseNum) {
        this.courseNum = courseNum;
    }

    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTermNum() {
        return termNum;
    }

    public String getCourseNum() {
        return courseNum;
    }
}
