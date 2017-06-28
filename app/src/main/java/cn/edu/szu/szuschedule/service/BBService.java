package cn.edu.szu.szuschedule.service;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.CookieManager;
import android.webkit.WebView;

import cn.edu.szu.szuschedule.object.*;
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
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
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
    
    private static WebView webView;
    private static Application mApplication;

    private static boolean getttingSubjectItems = false;
    private static boolean getttingHomework = false;

    // 数据区
    private static List<SubjectItem> subjectItems = new ArrayList<>();
    private static List<Homework> mHomeworkList = new ArrayList<>();
    private static HashMap<SubjectItem, List<Homework>> subjectItemListHashMap = new HashMap<>();
    private static HashMap<Homework, List<Attachment>> homeworkListHashMap = new HashMap<>();

    private static List<OnDataChangedListener> onDataChangedListeners = new ArrayList<>();

    // 数据改变接口
    public interface OnDataChangedListener {
        void onSubjectItemsChanged(List<SubjectItem> subjectItems);
        void onHomeworkChanged(List<Homework> homeworkList);
    }

    public static void addOnDataChangedListener(OnDataChangedListener onDataChangedListener) {
        if (onDataChangedListener != null) {
            onDataChangedListeners.add(onDataChangedListener);
            onDataChangedListener.onSubjectItemsChanged(subjectItems);
            onDataChangedListener.onHomeworkChanged(mHomeworkList);
        }
    }

    public static void removeOnDataChangedListener(OnDataChangedListener onDataChangedListener) {
        onDataChangedListeners.remove(onDataChangedListener);
    }

    /**
     * 通知subjectItems被修改
     */
    private static void dispatcherSubjectItemsChanged() {
        for (OnDataChangedListener onDataChangedListener : onDataChangedListeners) {
            onDataChangedListener.onSubjectItemsChanged(subjectItems);
        }
    }

    /**
     * 通知homework被修改
     */
    private static void dispatcherHomeworkChanged() {
        for (OnDataChangedListener onDataChangedListener : onDataChangedListeners) {
            onDataChangedListener.onHomeworkChanged(mHomeworkList);
        }
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
                webView.setWebViewClient(new SZUAuthenticationWebViewClient(username, password, e, loginPageUrl, bbUrl));
                webView.loadUrl(enterBBUrl);
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 初始化Service数据
     * 应用启动并完成登录后调用
     */
    public static void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 从本地获取数据
                getSubjectFromLocalDatabase();
                getAllHomeworkFromLocalDatabase();
                getAllAttachments();

                // 从网络获取数据
                if (subjectItems.size() == 0) {
                    User user = UserService.getCurrentUser();
                    loginBB(user.getAccount(), user.getPassword())
                            .observeOn(Schedulers.io())
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    refreshSubject();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                }
                            });
                }
            }
        }).start();
    }

    /**
     * 刷新科目列表
     */
    public static void refreshSubject() {
        getSubjectFromNetwork();
        getAllHomeworkFromNetwork();
    }

    /**
     * 刷新作业列表
     */
    public static void refreshHomework() {

    }

    /**
     * 本地获取科目列表
     */
    private static void getSubjectFromLocalDatabase(){
        SQLiteDatabase db = DBHelper.getDB(mApplication);
        Cursor cursor = db.rawQuery("SELECT subject.* FROM blackboard INNER JOIN subject ON blackboard.studentID = ? AND blackboard.subjectID = subject.id", new String[] {String.valueOf(UserService.getCurrentUser().getId())});
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
            dispatcherSubjectItemsChanged();
        }
        cursor.close();
        db.close();
    }

    /**
     * 从本地数据库获取所有科目的作业列表
     */
    private static void getAllHomeworkFromLocalDatabase() {
        SQLiteDatabase db = DBHelper.getDB(mApplication);
        for (SubjectItem subjectItem : subjectItems) {
            List<Homework> homeworkList = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT homework.* FROM subjectHomeworkMap INNER JOIN homework ON subjectHomeworkMap.subjectID = ? AND homework.id = subjectHomeworkMap.homeworkID", new String[]{String.valueOf(subjectItem.getId())});
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
                        cursor.getInt(finishedIndex),
                        subjectItem
                );
                homeworkList.add(homework);
                mHomeworkList.add(homework);
                dispatcherHomeworkChanged();
            }
            subjectItemListHashMap.put(subjectItem, homeworkList);
            cursor.close();
        }
        db.close();
    }

    /**
     * 获取所有作业的附件列表
     * @return
     */
    public static void getAllAttachments() {
        SQLiteDatabase db = DBHelper.getDB(mApplication);
        for (Homework homework : mHomeworkList) {
            List<Attachment> attachments = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT attachment.* FROM homeworkAttachmentMap INNER JOIN attachment ON homeworkAttachmentMap.homeworkID = ? AND attachment.id = homeworkAttachmentMap.attachmentID", new String[]{String.valueOf(homework.getId())});
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
        }
        db.close();
    }

    /**
     * 获取指定作业的附件
     * @param homework
     */
    public static List<Attachment> getAttachment(Homework homework) {
        return homeworkListHashMap.get(homework);
    }

    /**
     * 从外网获取科目列表
     * @return
     */
    private static void getSubjectFromNetwork() {
        try {
            String html = OkGo.post(courseUrl)
                    .headers("Cookie",CookieManager.getInstance().getCookie(baseUrl))
                    .params("action","refreshAjaxModule")
                    .params("modId","_25_1")
                    .params("tabId","_2_1")
                    .params("tab_tab_group_id","_2_1")
                    .execute().body().string();
            Pattern pattern = Pattern.compile("<a href=\"(.*?)\".*>(.*?)</a>");
            Matcher matcher = pattern.matcher(html);
            SQLiteDatabase db = DBHelper.getDB(mApplication);
            // 清空原有数据
            boolean flag = true;
            while(matcher.find()){
                if (flag) {
                    clearDatabase(db);
                    flag = false;
                }
                String link = matcher.group(1).trim();
                String info = matcher.group(2).trim();
                String[] str = link.split("%3D");
                SubjectItem course = new SubjectItem(
                        -1,
                        info.substring(info.indexOf(":") + 2),
                        str[str.length-1].split("%26")[0],
                        info.substring(6, info.indexOf(":")),
                        info.substring(0, 5));
                saveSubjectToDataBase(course, db);
                subjectItems.add(course);
                dispatcherSubjectItemsChanged();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除当前用户所有记录
     * @param db
     */
    private static void clearDatabase(SQLiteDatabase db) {
        clearHomeworkData(db);
        for (SubjectItem subjectItem : subjectItems) {
            db.delete("subject", "id=?", new String[] {String.valueOf(subjectItem)});
        }
        db.execSQL("DELETE FROM blackboard WHERE studentID = ?", new String[] {String.valueOf(UserService.getCurrentUser().getId())});
        subjectItems.clear();
        subjectItemListHashMap.clear();
    }

    /**
     * 保存科目信息到数据库中
     * @param subjectItem
     */
    private static void saveSubjectToDataBase(SubjectItem subjectItem, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put("subjectName", subjectItem.getSubjectName());
        cv.put("courseNum", subjectItem.getCourseNum());
        cv.put("termNum", subjectItem.getTermNum());
        cv.put("courseId", subjectItem.getTermNum());
        db.insert("subject", null, cv);
        // 获取最后插入的记录id
        Cursor cursor = db.rawQuery("select last_insert_rowid() from subject", null);
        if (cursor.moveToFirst()) {
            subjectItem.setId(cursor.getInt(0));
        }
        cursor.close();

        cv = new ContentValues();
        cv.put("studentID", UserService.getCurrentUser().getId());
        cv.put("subjectID", subjectItem.getId());
        db.insert("blackboard", null, cv);
    }


    /**
     * 从网络上获取所有课程的作业列表
     * 并保存到数据库中
     */
    private static void getAllHomeworkFromNetwork() {
        String cookie = CookieManager.getInstance().getCookie(baseUrl);
        boolean flag = true;
        for (SubjectItem subjectItem : subjectItems) {
            try {
                List<Homework> homeworkList = new ArrayList<>();
                Response response = OkGo.<String>get(String.format("http://elearning.szu.edu.cn/webapps/blackboard/execute/modulepage/view?course_id=%s&mode=view", subjectItem.getCourseId()))
                        .headers("Cookie", cookie)
                        .execute();
                String html = response.body().string();
                Pattern pattern = Pattern.compile("<a href=\"(.*?)\" target=\"_self\"><span title=\"网上作业\">网上作业</span></a>");
                Matcher matcher = pattern.matcher(html);
                if (!matcher.find()) {
                    // 找不到网上作业的地址，说明该课程没有作业。直接跳过
                    continue;
                }
                String homeworkUrl = matcher.group(1);
                response = OkGo.<String>get(homeworkUrl)
                        .headers("Cookie", cookie)
                        .execute();
                html = response.body().string();
                pattern = Pattern.compile("<li[\\w\\W]*?id=\"contentListItem[\\w\\W]*?>[\\w\\W]*?<a href=\"([\\w\\W]*?)\"><span style=\"color:#000000;\">([\\w\\W]*?)</span>[\\w\\W]*?(<ul class=\"attachments clearfix\">([\\w\\W]*?)</ul>)?[\\w\\W]*?<div class=\"vtbegenerated\">([\\w\\W]*?)<div id=\"[\\w\\W]*?</li>");
                matcher = pattern.matcher(html);
                SQLiteDatabase db = DBHelper.getDB(mApplication);
                while (matcher.find()) {
                    if (flag) {
                        clearHomeworkData(db);
                        flag = false;
                    }
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
                            finished,
                            subjectItem);
                    saveHomeworkToDatabase(db, homework, subjectItem, attachments);
                    homeworkListHashMap.put(homework, attachments);
                    mHomeworkList.add(homework);
                    homeworkList.add(homework);
                    dispatcherHomeworkChanged();
                }
                subjectItemListHashMap.put(subjectItem, homeworkList);
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 清除当前用户的所有作业信息
     * @param db
     */
    private static void clearHomeworkData(SQLiteDatabase db) {
        homeworkListHashMap.clear();
        mHomeworkList.clear();
        for (SubjectItem subjectItem : subjectItems) {
            subjectItemListHashMap.put(subjectItem, new ArrayList<Homework>());
            Cursor cursor = db.rawQuery("SELECT homework.id FROM subjectHomeworkMap INNER JOIN homework ON subjectHomeworkMap.subjectID = ? AND subjectHomeworkMap.homeworkID = homework.id", new String[] {String.valueOf(subjectItem.getId())});
            while (cursor.moveToNext()) {
                String homeworkID = cursor.getString(0);
                Cursor cursor2 = db.rawQuery("SELECT attachment.id FROM homeworkAttachmentMap INNER JOIN attachment ON homeworkAttachmentMap.homeworkID = ? AND homeworkAttachmentMap.attachmentID = attachment.id", new String[] {homeworkID});
                while (cursor2.moveToNext()) {
                    db.delete("attachment", "id=?", new String[] {cursor2.getString(0)});
                    db.delete("homeworkAttachmentMap", "attachmentID=?", new String[] {cursor2.getString(0)});
                }
                db.delete("homework", "id=?", new String[] {homeworkID});
                db.delete("subjectHomeworkMap", "homeworkID=?", new String[] {homeworkID});
            }
        }
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

    /**
     * 清除现有数据对象
     */
    public static void clearCurrentData() {
        subjectItems = null;
        subjectItemListHashMap.clear();
        homeworkListHashMap.clear();
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
    public static void init(Application app) {
        // 初始化WebView
        webView = new WebView(app);
        mApplication = app;
        CommonUtil.initWebView(webView);
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
                        Matcher matcher = Pattern.compile("<input.*id=\"studentId\".*value=\"(.*?)\".*/>").matcher(s);
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
     * 获取指定学期的课程
     * @return
     */
    public static List<SubjectItem> getCoursesByTerm(List<SubjectItem> subjectItems, String termNum) {
        List<SubjectItem> currentTerm = new ArrayList<>();
        for (SubjectItem subjectItem : subjectItems) {
            if (termNum.equals(subjectItem.getTermNum())) {
                currentTerm.add(subjectItem);
            }
        }
        return currentTerm;
    }

}
