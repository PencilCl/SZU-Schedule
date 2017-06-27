package cn.edu.szu.szuschedule.service;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.CookieManager;
import android.webkit.WebView;

import cn.edu.szu.szuschedule.object.SubjectItem;
import cn.edu.szu.szuschedule.util.CommonUtil;
import cn.edu.szu.szuschedule.util.SZUAuthenticationWebViewClient;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lzy.okgo.convert.StringConvert;
import com.lzy.okrx2.adapter.ObservableBody;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chenlin on 14/06/2017.
 * 使用BBService前必须先调用init方法
 */
public class BBService {
    private final static String baseUrl = "http://elearning.szu.edu.cn";
    private final static String loginPageUrl = "https://authserver.szu.edu.cn/authserver/login(.*?)?service=https%3a%2f%2fauth.szu.edu.cn%2fcas.aspx%2flogin%3fservice%3dhttp%3a%2f%2felearning.szu.edu.cn%2fwebapps%2fcbb-sdgxtyM-BBLEARN%2fgetuserid.jsp";
    private final static String enterBBUrl = "http://elearning.szu.edu.cn/webapps/cbb-sdgxtyM-BBLEARN/checksession.jsp";
    private final static String bbUrl = "http://elearning.szu.edu.cn/webapps/portal/frameset.jsp"; // 成功进入bb的页面
    private final static String stuNumUrl = "http://elearning.szu.edu.cn/webapps/blackboard/execute/editUser?context=self_modify"; // 获取学号url
    private final static String courseUrl = "http://elearning.szu.edu.cn/webapps/portal/execute/tabs/tabAction";

    private final static String stuNumReg = "<input.*id=\"studentId\".*value=\"(.*?)\".*/>";
    private static Pattern stuNumPattern;

    private WebView webView;
    private static ArrayList<SubjectItem> subjectItems;
    private BBService() {
        stuNumPattern = Pattern.compile(stuNumReg);
    }

    public static BBService getInstance() {
        return BBServiceHolder.holder;
    }

    private static class BBServiceHolder {
        private static BBService holder = new BBService();
    }

    /**
     * 初始化BBService
     */
    public void init(Application app) {
        // 初始化WebView
        this.webView = new WebView(app);
        CommonUtil.initWebView(this.webView);
    }

    /**
     * 登录bb
     * 使用bb相关功能前应当调用此方法，确保完成了bb的登录
     * @param username 校园卡号
     * @param password 统一身份认证密码
     * @return 登录失败抛出异常，登录成功返回空字符串
     */
    public static Observable<String> loginBB(final String username, final String password) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                BBService bbService = getInstance();
                bbService.webView.setWebViewClient(new SZUAuthenticationWebViewClient(username, password, e, loginPageUrl, bbUrl));
                bbService.webView.loadUrl(enterBBUrl);
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取当前登录用户的学号
     * @return 成功返回学号，失败返回空字符
     */
    public static Observable<String> getStuNum() {
        CookieManager cookieManager = CookieManager.getInstance();
        return OkGo.<String>get(stuNumUrl)
                .headers("Cookie", cookieManager.getCookie(baseUrl))
                .converter(new StringConvert())
                .adapt(new ObservableBody<String>())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        Matcher matcher = stuNumPattern.matcher(s);
                        if (matcher.find()) {
                            return matcher.group(1);
                        } else {
                            return "";
                        }
                    }
                })
                .subscribeOn(Schedulers.io());
    }
    public static Observable<ArrayList<SubjectItem>> getAllCourses(final Context context) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<SubjectItem>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<SubjectItem>> e) throws Exception {
                if(subjectItems  == null){
                    subjectItems = new ArrayList<>();
                } else{
                    e.onNext(subjectItems);
                    return;
                }
                getLocalDatabaseData(context);
                // 如果本地数据为空，则从网络中获取
                if (subjectItems.size() == 0) {
                    String error = getDataFromNetwork(context);
                    if (error == null) {
                        e.onNext(subjectItems);
                    } else {
                        e.onError(new Throwable(error));
                    }
                } else {
                    e.onNext(subjectItems);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public static Observable<ArrayList<SubjectItem>> updateAllCourses(final Context context) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<SubjectItem>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<SubjectItem>> e) throws Exception {
                if(subjectItems  == null){
                    subjectItems = new ArrayList<>();
                }
                System.out.println("updateAllCoures");
                    String error = getDataFromNetwork(context);
                    if (error == null) {
                        e.onNext(subjectItems);
                    } else {
                        e.onError(new Throwable(error));
                    }
                }

        }).subscribeOn(Schedulers.io());
    }
    //从本地获取数据
    private  static void getLocalDatabaseData(Context context){
        SQLiteDatabase db = DBHelper.getDB(context);
        Cursor cursor = db.rawQuery("SELECT * FROM subject",null);
        int idIndex = cursor.getColumnIndex("id");
        int subjectNameIndex = cursor.getColumnIndex("subjectName");
        int courseNumIndex = cursor.getColumnIndex("courseNum");
        int termNumIndex = cursor.getColumnIndex("termNum");
        int courseIDIndex = cursor.getColumnIndex("courseId");
        while(cursor.moveToNext()){
            SubjectItem subjectItem = new SubjectItem(
                    cursor.getInt(idIndex),
                    cursor.getString(subjectNameIndex),
                    cursor.getString(courseIDIndex),
                    cursor.getString(courseNumIndex),
                    cursor.getString(termNumIndex)
                    );
            System.out.println("本地获取      "+ subjectItem.getCourseNum());

            subjectItems.add(subjectItem);
        }
        cursor.close();
        db.close();
    }
    //从外网获取
    private  static String getDataFromNetwork(Context context){
        CookieManager cookieManager = CookieManager.getInstance();
        if(cookieManager.getCookie(courseUrl) == null){
            return "获取课程信息失败";
        }
        try{

            okhttp3.Response response = OkGo.post(courseUrl)
                    .headers("Cookie",cookieManager.getCookie(baseUrl))
                    .params("action","refreshAjaxModule")
                    .params("modId","_25_1")
                    .params("tabId","_2_1")
                    .params("tab_tab_group_id","_2_1")
                    .execute();
            String html = response.body().string();
            Pattern pattern = Pattern.compile("<a href=\"(.*?)\".*>(.*?)</a>");
            Matcher matcher = pattern.matcher(html);
            SQLiteDatabase db = DBHelper.getDB(context);
            //清空原有数据
            db.execSQL("DELETE FROM subject");
            subjectItems.clear();
            while(matcher.find()){
                String link = matcher.group(1).trim();
                String info = matcher.group(2).trim();
                String courseName = info.substring(info.indexOf(":") + 2);
                String termNum = info.substring(0, 5);
                String courseNum = info.substring(6, info.indexOf(":"));
                String[] str = link.split("%3D");
                String courseId = str[str.length-1].split("%26")[0];
                SubjectItem course = new SubjectItem(-1,courseName,courseId,courseNum,termNum);
                ContentValues cv = new ContentValues();
                cv.put("subjectName",courseName);
                cv.put("courseNum",courseNum);
                cv.put("termNum",termNum);
                cv.put("courseId",courseId);
                db.insert("subject",null,cv);
                // 获取最后插入的记录id
                Cursor cursor = db.rawQuery("select last_insert_rowid() from subject", null);
                int lastId = 0;
                if(cursor.moveToFirst()) lastId = cursor.getInt(0);
                cursor.close();
                course.setId(lastId);
                subjectItems.add(course);
                System.out.println(String.format("学期号: %s\n课程名: %s\n课程号: %s\n链接: %s", termNum, courseName, courseNum, link));
                System.out.println("------------------------------");

            }
            return null;

        }catch (Exception e){
            e.printStackTrace();
        }
        return "发生未知错误";
    }
}
