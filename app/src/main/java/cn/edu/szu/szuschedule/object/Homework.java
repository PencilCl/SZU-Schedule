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

    public Homework(int id, String name, String description, Integer score, String deadline) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.score = score;
        this.deadline = deadline;
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
