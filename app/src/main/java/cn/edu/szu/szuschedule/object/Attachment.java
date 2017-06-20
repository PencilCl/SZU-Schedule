package cn.edu.szu.szuschedule.object;

/**
 * Created by Tongwen on 2017/6/20.
 */

public class Attachment {
    private String name;
    private String url;

    public Attachment(String name, String url){
        this.name = name;
        this.url = url ;
    }

    public String getName() {   return name;}

    public String getUrl()  {   return url; }
}
