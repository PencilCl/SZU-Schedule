package cn.edu.szu.szuschedule.service;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.CookieManager;
import android.webkit.WebView;

import cn.edu.szu.szuschedule.object.Attachment;
import cn.edu.szu.szuschedule.object.Homework;
import cn.edu.szu.szuschedule.object.SubjectItem;
import cn.edu.szu.szuschedule.object.TodoItem;
import cn.edu.szu.szuschedule.util.CommonUtil;
import cn.edu.szu.szuschedule.util.SZUAuthenticationWebViewClient;
import com.lzy.okgo.OkGo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import okhttp3.Response;

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
    private static HashMap<SubjectItem, List<Homework>> subjectItemListHashMap;
    private static HashMap<Homework, List<Attachment>> homeworkListHashMap = new HashMap<>();
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
     * 获取指定日期需要完成的作业
     * @param date
     * @return
     */
    public static List<TodoItem> getTodoList(Date date) {
        List<TodoItem> todoItems = new ArrayList<>();
        // TODO: 28/06/2017
        return todoItems;
    }

    /**
     * 初始化BBService
     */
    public void init(Application app) {
        // 初始化WebView
        this.webView = new WebView(app);
        CommonUtil.initWebView(this.webView);
        subjectItemListHashMap = new HashMap<>();
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
     * 获取作业的附件列表
     * @param homework
     * @return
     */
    public static Observable<List<Attachment>> getAttachments(final Context context, final Homework homework) {
        return Observable.create(new ObservableOnSubscribe<List<Attachment>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Attachment>> e) throws Exception {
                List<Attachment> attachments = homeworkListHashMap.get(homework);
                if (attachments != null) {
                    e.onNext(attachments);
                    return ;
                }
                attachments = new ArrayList<>();

                // 从数据库中获取
                SQLiteDatabase db = DBHelper.getDB(context);
                Cursor cursor = db.rawQuery("select attachment.* from homeworkAttachmentMap inner join attachment on homeworkAttachmentMap.homeworkID = ? and attachment.id = homeworkAttachmentMap.attachmentID", new String[]{String.valueOf(homework.getId())});
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("attachmentName");
                int urlIndex = cursor.getColumnIndex("attachmentUrl");
                while (cursor.moveToNext()) {
                    attachments.add(new Attachment(
                            cursor.getInt(idIndex),
                            cursor.getString(nameIndex),
                            cursor.getString(urlIndex)
                    ));
                }
                homeworkListHashMap.put(homework, attachments);
                e.onNext(attachments);
            }
        }).subscribeOn(Schedulers.io());
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

    /**
     * 获取当前用户所有科目列表
     * @param context
     * @return
     */
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
                getSubjectFromLocalDatabase(context);
                // 如果本地数据为空，则从网络中获取
                if (subjectItems.size() == 0) {
                    String error = getSubjectFromNetwork(context);
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

    /**
     * 更新科目列表
     * @param context
     * @return
     */
    public static Observable<ArrayList<SubjectItem>> updateAllCourses(final Context context) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<SubjectItem>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<SubjectItem>> e) throws Exception {
                if(subjectItems  == null){
                    subjectItems = new ArrayList<>();
                }
                String error = getSubjectFromNetwork(context);
                if (error == null) {
                    e.onNext(subjectItems);
                } else {
                    e.onError(new Throwable(error));
                }
                }

        }).subscribeOn(Schedulers.io());
    }

    /**
     * 本地获取科目列表
     * @param context
     */
    private  static void getSubjectFromLocalDatabase(Context context){
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
            subjectItems.add(subjectItem);
        }
        cursor.close();
        db.close();
    }

    /**
     * 从外网获取科目列表
     * @param context
     * @return
     */
    private  static String getSubjectFromNetwork(Context context){
        try{
            okhttp3.Response response = OkGo.post(courseUrl)
                    .headers("Cookie",CookieManager.getInstance().getCookie(baseUrl))
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
            }
            db.close();
            return null;

        }catch (Exception e){
            e.printStackTrace();
        }
        return "发生未知错误";
    }

    /**
     * 通过课程获取相应的作业信息
     * @return
     */
    public static Observable<List<Homework>> getHomework(final Context context, final SubjectItem subjectItem) {
        return Observable.create(new ObservableOnSubscribe<List<Homework>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Homework>> e) throws Exception {
                List<Homework> homeworkList = subjectItemListHashMap.get(subjectItem);
                if (homeworkList != null) {
                    e.onNext(homeworkList);
                    return ;
                }
                // 从本地数据库获取数据
                homeworkList = getHomeworkFromLocalDatabase(context, subjectItem);
                if (homeworkList.size() != 0) {
                    subjectItemListHashMap.put(subjectItem, homeworkList);
                    e.onNext(homeworkList);
                    return ;
                }
                // 从网络获取数据
                homeworkList = getHomeworkFromNetwork(context, subjectItem);
                if (homeworkList == null) {
                    e.onError(new Throwable("获取作业信息失败"));
                } else {
                    subjectItemListHashMap.put(subjectItem, homeworkList);
                    e.onNext(homeworkList);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 从本地数据库获取指定科目的作业列表
     * @param context
     * @param subjectItem
     * @return
     */
    private static List<Homework> getHomeworkFromLocalDatabase(Context context, SubjectItem subjectItem) {
        List<Homework> homeworkList = new ArrayList<>();
        SQLiteDatabase db = DBHelper.getDB(context);
        Cursor cursor = db.rawQuery("select homework.* from subjectHomeworkMap inner join homework on subjectHomeworkMap.subjectID = ? and homework.id = subjectHomeworkMap.homeworkID", new String[]{String.valueOf(subjectItem.getId())});
        int idIndex = cursor.getColumnIndex("id");
        int homeworkNameIndex = cursor.getColumnIndex("homeworkName");
        int descriptionIndex = cursor.getColumnIndex("description");
        int scoreIndex = cursor.getColumnIndex("score");
        int deadlineIndex = cursor.getColumnIndex("deadline");
        int finishedIndex = cursor.getColumnIndex("finished");

        while (cursor.moveToNext()) {
            Homework homework = new Homework(
                    cursor.getInt(idIndex),
                    cursor.getString(homeworkNameIndex),
                    cursor.getString(descriptionIndex),
                    cursor.getInt(scoreIndex),
                    cursor.getString(deadlineIndex),
                    cursor.getInt(finishedIndex)
            );
            homeworkList.add(homework);
        }
        db.close();
        cursor.close();
        return homeworkList;
    }

    /**
     * 从网络上获取指定课程的作业列表
     * 并保存到数据库中
     * @param context
     * @param subjectItem
     * @return 成功返回作业数组, 失败返回null
     */
    private static List<Homework> getHomeworkFromNetwork(Context context, SubjectItem subjectItem) {
        String cookie = CookieManager.getInstance().getCookie(baseUrl);
        try {
            List<Homework> homeworkList = new ArrayList<>();
            Response response = OkGo.<String>get(String.format("http://elearning.szu.edu.cn/webapps/blackboard/execute/modulepage/view?course_id=%s&mode=view", subjectItem.getCourseId()))
                    .headers("Cookie", cookie)
                    .execute();
            String html = response.body().string();
            Pattern pattern = Pattern.compile("<a href=\"(.*?)\" target=\"_self\"><span title=\"网上作业\">网上作业</span></a>");
            Matcher matcher = pattern.matcher(html);
            if (!matcher.find()) {
                // 找不到网上作业的地址，说明该课程没有作业。直接返回空数组
                return homeworkList;
            }
            String homeworkUrl = matcher.group(1);
            response = OkGo.<String>get(homeworkUrl)
                    .headers("Cookie", cookie)
                    .execute();
            html = response.body().string();
            pattern = Pattern.compile("<li[\\w\\W]*?id=\"contentListItem[\\w\\W]*?>[\\w\\W]*?<a href=\"([\\w\\W]*?)\"><span style=\"color:#000000;\">([\\w\\W]*?)</span>[\\w\\W]*?(<ul class=\"attachments clearfix\">([\\w\\W]*?)</ul>)?[\\w\\W]*?<div class=\"vtbegenerated\">([\\w\\W]*?)<div id=\"[\\w\\W]*?</li>");
            matcher = pattern.matcher(html);
            SQLiteDatabase db = DBHelper.getDB(context);
            while (matcher.find()) {
                List<Attachment> attachments = new ArrayList<>();
                String singleHomeworkHtml = matcher.group(0);
                Pattern singleHomeworkPattern;
                boolean hasAttachment = singleHomeworkHtml.contains("<th scope=\"row\">已附加文件:</th>");
                if (hasAttachment) {
                    singleHomeworkPattern = Pattern.compile("<li[\\w\\W]*?id=\"contentListItem[\\w\\W]*?>[\\w\\W]*?<a href=\"([\\w\\W]*?)\"><span style=\"color:#000000;\">([\\w\\W]*?)</span>[\\w\\W]*?<ul class=\"attachments clearfix\">([\\w\\W]*?)</ul>[\\w\\W]*?<div class=\"vtbegenerated\">([\\w\\W]*?)<div id=\"[\\w\\W]*?</li>");
                } else {
                    singleHomeworkPattern = Pattern.compile("<li[\\w\\W]*?id=\"contentListItem[\\w\\W]*?>[\\w\\W]*?<a href=\"([\\w\\W]*?)\"><span style=\"color:#000000;\">([\\w\\W]*?)</span>[\\w\\W]*?<div class=\"vtbegenerated\">([\\w\\W]*?)<div id=\"[\\w\\W]*?</li>");
                }

                Matcher singleHomeworkMatcher = singleHomeworkPattern.matcher(singleHomeworkHtml);
                if (!singleHomeworkMatcher.find()) {
                    System.out.println("not found");
                    continue;
                }

                if (hasAttachment) {
                    Pattern attachmentPattern = Pattern.compile("<a href=\"([\\w\\W]*?)\" target=\"_blank\">[\\w\\W]*?&nbsp;([\\w\\W]*?)</a>");
                    Matcher attachmentMatcher = attachmentPattern.matcher(singleHomeworkMatcher.group(3));
                    while (attachmentMatcher.find()) {
                        attachments.add(new Attachment(-1, attachmentMatcher.group(2), attachmentMatcher.group(1)));
                    }
                }

                response = OkGo.<String>get(baseUrl + singleHomeworkMatcher.group(1))
                        .headers("Cookie", cookie)
                        .execute();
                html = response.body().string();
                String deadline = null;
                int score = -1;
                Pattern deadlinePattern = Pattern.compile("id=\"dueDate\" value=\"(.*?)\"");
                Matcher deadlineMatcher = deadlinePattern.matcher(html);
                if (deadlineMatcher.find()) {
                    deadline = deadlineMatcher.group(1);
                    if ("&nbsp;".equals(deadline)) {
                        deadline = "null";
                    }
                }
                int finished = 0;
                int index1 = html.indexOf("/webapps/blackboard/execute/attemptExpandListItemGenerator?attempt_id=");
                if (index1 == -1) {
                    index1 = html.indexOf("/webapps/blackboard/execute/groupAttemptExpandListItemGenerator?groupAttempt_id=");
                }
                if (index1 != -1) {
                    finished = 1;
                    String scoreUrl = html.substring(index1, html.indexOf("\"", index1));
                    String scoreHtml = OkGo.<String>post(baseUrl + scoreUrl)
                            .headers("Cookie", cookie)
                            .params("course_id", subjectItem.getCourseId())
                            .params("filterAttemptHref", "%2Fwebapps%2Fblackboard%2Fexecute%2FgradeAttempt")
                            .execute().body().string();
                    Pattern scorePattern = Pattern.compile("成绩 :.*?([0-9]+).*");
                    Matcher scoreMatcher = scorePattern.matcher(scoreHtml);
                    if (scoreMatcher.find()) {
                        score = Integer.valueOf(scoreMatcher.group(1).trim());
                    }
                }
                Homework homework = new Homework(
                        -1,
                        singleHomeworkMatcher.group(2),
                        singleHomeworkMatcher.group(hasAttachment ? 4 : 3).replaceAll("</div>", "\n").replaceAll("<div>", "").trim(),
                        score,
                        deadline,
                        finished);
                homeworkListHashMap.put(homework, attachments);
                saveHomeworkToDatabase(db, homework, subjectItem, attachments);
                homeworkList.add(homework);
            }
            db.close();
            return homeworkList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存作业信息到数据库中
     * @param db
     * @param homework
     * @param subjectItem
     * @param attachments
     */
    private static void saveHomeworkToDatabase(SQLiteDatabase db, Homework homework, SubjectItem subjectItem, List<Attachment> attachments) {
        // 保存到homework表中
        ContentValues cv = new ContentValues();
        cv.put("homeworkName", homework.getName());
        cv.put("description", homework.getDescription());
        cv.put("deadline", homework.getDeadline());
        cv.put("score", homework.getScore());
        cv.put("finished", homework.isFinished() ? 1 : 0);
        db.insert("homework", null, cv);
        // 获取最后插入的记录id
        Cursor cursor = db.rawQuery("select last_insert_rowid() from homework", null);
        int lastId = 0;
        if (cursor.moveToFirst()) lastId = cursor.getInt(0);
        cursor.close();
        homework.setId(lastId);

        // 保存附件列表
        for (Attachment attachment : attachments) {
            cv = new ContentValues();
            cv.put("attachmentName", attachment.getName());
            cv.put("attachmentUrl", attachment.getUrl());
            db.insert("attachment", null, cv);
            // 获取最后插入的记录id
            cursor = db.rawQuery("select last_insert_rowid() from attachment", null);
            lastId = 0;
            if (cursor.moveToFirst()) lastId = cursor.getInt(0);
            cursor.close();
            attachment.setId(lastId);
            cursor.close();
            // 保存到homeworkAttachmentMap表中
            cv = new ContentValues();
            cv.put("homeworkID", homework.getId());
            cv.put("attachmentID", lastId);
            db.insert("homeworkAttachmentMap", null, cv);
        }

        // 保存到subjectHomeworkMap表中
        cv = new ContentValues();
        cv.put("subjectID", subjectItem.getId());
        cv.put("homeworkID", homework.getId());
        db.insert("subjectHomeworkMap", null, cv);
    }
}
