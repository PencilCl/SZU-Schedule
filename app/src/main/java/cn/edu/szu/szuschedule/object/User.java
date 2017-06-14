package cn.edu.szu.szuschedule.object;

/**
 * Created by chenlin on 10/06/2017.
 */
public class User {
    private String account;
    private String password;
    private String sex;
    private String stuNum;

    public User(String account, String password) {
        this(account, password, null, null);
    }

    public User(String account, String password, String sex, String stuNum) {
        this.account = account;
        this.password = password;
        this.sex = sex;
        this.stuNum = stuNum;
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

}
