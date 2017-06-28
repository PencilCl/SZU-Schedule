package cn.edu.szu.szuschedule.object;

/**
 * Created by chenlin on 07/06/2017.
 */
public class Homework {
    private int id;
    private String name; // 作业名称
    private String description ;    //作业概括
    private Integer score; // 得分
    private String deadline; //截止日期
    private boolean finished;

    public Homework(int id, String name, String description, Integer score, String deadline, int finished) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.score = score;
        this.deadline = deadline;
        this.finished = finished == 1;
    }

    public void setFinished(int finished) {
        this.finished = finished == 1;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getScore() {
        return score;
    }

    public String getDeadline() {
        return deadline;
    }
}
