package cn.edu.szu.szuschedule.object;

/**
 * Created by chenlin on 10/06/2017.
 */
public class User {
    private int id;
    private String account;
    private String password;
    private String sex;
    private String stuNum;
    private String name;
    public User(int id, String account, String password, String stuNum) {
        this(id, account, password, null, stuNum, null);
    }

    public User(int id, String account, String password, String sex, String stuNum,String name) {
        this.account = account;
        this.password = password;
        this.sex = sex;
        this.stuNum = stuNum;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setStuNum(String stuNum) {
        this.stuNum = stuNum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getSex() {
        return sex;
    }

    public String getStuNum() {
        return stuNum;
    }

    public String getName() {   return name;    }

}
