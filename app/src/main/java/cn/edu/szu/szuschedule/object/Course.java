package cn.edu.szu.szuschedule.object;

/**
 * Created by chenlin on 07/06/2017.
 */
public class Course {
    private int id;
    private String courseName;
    private String venue;
    private int day;
    private int begin;
    private int end;

    public Course(int id, String courseName, String venue, int day, int begin, int end) {
        this.id = id;
        this.courseName = courseName;
        this.venue = venue;
        this.day = day;
        this.begin = begin;
        this.end = end;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getVenue() {
        return venue;
    }

    public int getDay() {
        return day;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }
}
