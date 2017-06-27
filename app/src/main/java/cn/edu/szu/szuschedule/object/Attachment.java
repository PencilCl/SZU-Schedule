package cn.edu.szu.szuschedule.object;

/**
 * Created by Tongwen on 2017/6/20.
 */

public class Attachment {
    private int id;
    private String name;
    private String url;

    public Attachment(int id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url ;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getName() {   return name;}

    public String getUrl()  {   return url; }
}
