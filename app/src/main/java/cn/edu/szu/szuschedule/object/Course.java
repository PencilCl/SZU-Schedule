package cn.edu.szu.szuschedule.object;

/**
 * Created by chenlin on 07/06/2017.
 */
public class Course {
    private String courseName;
    private String venue;
    private int day;
    private int begin;
    private int end;

    public Course(String courseName, String venue, int day, int begin, int end) {
        this.courseName = courseName;
        this.venue = venue;
        this.day = day;
        this.begin = begin;
        this.end = end;
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
