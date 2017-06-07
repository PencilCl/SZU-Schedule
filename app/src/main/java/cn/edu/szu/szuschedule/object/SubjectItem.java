package cn.edu.szu.szuschedule.object;

/**
 * Created by jazzyzhong on 2017/6/7.
 */
public class SubjectItem {
    private String subname;
    private String substatus;

    public SubjectItem(String name,String status){
        this.subname = name;
        this.substatus = status;
    }
    public String getSubname(){return this.subname;}
    public String getSubstatus(){return this.substatus;}
}
