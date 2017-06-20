package cn.edu.szu.szuschedule.object;

/**
 * Created by chenlin on 10/06/2017.
 */
public class User {
    private String account;
    private String password;
    private String sex;
    private String stuNum;
    private String name;
    public User(String account, String password,String name) {
        this(account, password, null, null,name);
    }

    public User(String account, String password, String sex, String stuNum,String name) {
        this.account = account;
        this.password = password;
        this.sex = sex;
        this.stuNum = stuNum;
        this.name = name;
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
