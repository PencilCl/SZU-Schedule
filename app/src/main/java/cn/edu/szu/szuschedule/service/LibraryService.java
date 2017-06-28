package cn.edu.szu.szuschedule.service;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.*;
import cn.edu.szu.szuschedule.object.BookItem;
import cn.edu.szu.szuschedule.object.TodoItem;
import cn.edu.szu.szuschedule.object.User;
import cn.edu.szu.szuschedule.util.SZUAuthenticationWebViewClient;
import com.lzy.okgo.OkGo;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.edu.szu.szuschedule.util.CommonUtil.initWebView;

/**
 * Created by chenlin on 27/06/2017.
 * 使用LibraryService前必须先调用init方法
 */
public class LibraryService {
    private final static String loginPageUrl = "https://authserver.szu.edu.cn/authserver/login(.*?)?service=https%3a%2f%2fauth.szu.edu.cn%2fcas.aspx%2flogin%3fservice%3dhttp%253a%252f%252fauth.lib.szu.edu.cn%252fextcas.aspx%252flogin%253fservice%253dhttp%25253a%25252f%25252fopac.lib.szu.edu.cn%25253a80%25252fopac%25252flogin.aspx%25253fReturnUrl%25253d%2525252fopac%2525252fuser%2525252fbookborrowed.aspx";
    private final static String booksUrl = "http://opac.lib.szu.edu.cn/opac/user/bookborrowed.aspx";
    private final static String reg = "<TD width=\"10%\">(.*?)</TD>\\s*?" +
            "<TD width=\"35%\"><[\\s\\S]*?>([\\s\\S]*?)[／/][\\s\\S]*?</a>" +
            "[\\s\\S]*?<TD width=\"10%\">(.*?)</TD>";

    private static WebView webView;
    private static Application mApplication;
    private static List<OnDataChangedListener> onDataChangedListeners = new ArrayList<>();

    // 数据区
    private static List<BookItem> bookItems = new ArrayList<>();

    public interface OnDataChangedListener {
        void onBookItemsChanged(List<BookItem> bookItems);
    }

    public static void addOnDataChangedListener(OnDataChangedListener onDataChangedListener) {
        if (onDataChangedListener != null) {
            onDataChangedListeners.add(onDataChangedListener);
            onDataChangedListener.onBookItemsChanged(bookItems);
        }
    }

    public static void removeOnDataChangedListener(OnDataChangedListener onDataChangedListener) {
        onDataChangedListeners.remove(onDataChangedListener);
    }

    private static void dispatcherBookItemsChanged() {
        for (OnDataChangedListener onDataChangedListener : onDataChangedListeners) {
            onDataChangedListener.onBookItemsChanged(bookItems);
        }
    }

    /**
     * 登录图书馆
     * @param username 校园卡号
     * @param password 统一身份认证密码
     * @return 登录失败抛出异常，登录成功返回空字符串
     */
    private static Observable<String> loginLibrary(final String username, final String password) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                webView.setWebViewClient(new SZUAuthenticationWebViewClient(username, password, e, loginPageUrl, booksUrl));
                webView.loadUrl(booksUrl);
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 初始化方法
     * @param app
     */
    public static void init(Application app) {
        // 初始化WebView
        mApplication = app;
        webView = new WebView(app);
        initWebView(webView);
    }

    /**
     * 初始化LibraryService数据
     * 应在登录完成后开始调用
     */
    public static void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 从本地获取数据
                getLocalDatabaseData();

                if (bookItems.size() == 0) {
                    User user = UserService.getCurrentUser();
                    loginLibrary(user.getAccount(), user.getPassword())
                            .observeOn(Schedulers.io())
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    getDataFromNetwork();
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
     * 刷新图书信息
     */
    public static void refresh() {
        getDataFromNetwork();
    }

    /**
     * 从本地数据库加载数据
     */
    private static void getLocalDatabaseData() {
        SQLiteDatabase db = DBHelper.getDB(mApplication);
        User user = UserService.getCurrentUser();
        Cursor cursor = db.rawQuery("SELECT * FROM library where library.studentID = ?", new String[] {String.valueOf(user.getId())});
        int idIndex = cursor.getColumnIndex("id");
        int bookNameIndex = cursor.getColumnIndex("bookName");
        int startDateIndex = cursor.getColumnIndex("startDate");
        int endDateIndex = cursor.getColumnIndex("endDate");
        while (cursor.moveToNext()) {
            BookItem bookmark = new BookItem(
                    cursor.getInt(idIndex),
                    cursor.getString(bookNameIndex),
                    cursor.getString(startDateIndex),
                    cursor.getString(endDateIndex)
            );

            bookItems.add(bookmark);
            dispatcherBookItemsChanged();
        }
        cursor.close();
        db.close();
    }


    /**
     * 从网络上获取借阅数据
     * 并保存到数据库中
     * @return 成功返回null 失败返回错误信息;
     */
    private static void getDataFromNetwork() {
        try {
            String html = OkGo.<String>get(booksUrl)
                    .headers("Cookie", CookieManager.getInstance().getCookie(booksUrl))
                    .execute().body().string();
            Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(html);
            SQLiteDatabase db = DBHelper.getDB(mApplication);
            //清空原来的内容
            bookItems.clear();
            dispatcherBookItemsChanged();
            clearDatabase(db);
            while(matcher.find()) {
                BookItem book = new BookItem(-1, matcher.group(2), matcher.group(3), matcher.group(1));
                saveBookItemToDatabase(book, db);
                bookItems.add(book);
                dispatcherBookItemsChanged();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除数据库中当前用户的图书相关信息
     */
    private static void clearDatabase(SQLiteDatabase db) {
        db.delete("library", "studentID = ?", new String[]{String.valueOf(UserService.getCurrentUser().getId())});
    }

    private static void saveBookItemToDatabase(BookItem bookItem, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put("studentID", UserService.getCurrentUser().getId());
        cv.put("bookName", bookItem.getBookName());
        cv.put("startDate", bookItem.getStartDate());
        cv.put("endDate", bookItem.getEndDate());
        db.insert("library", null, cv);
        // 获取最后插入的记录id
        Cursor cursor = db.rawQuery("select last_insert_rowid() from library", null);
        if (cursor.moveToFirst()) bookItem.setId(cursor.getInt(0));
        cursor.close();
    }

    /**
     * 清除当前对象数据
     */
    public static void clearCurrentData() {
        bookItems.clear();
    }

    /**
     * 获取指定日期需要归还的书本
     * @param date
     * @return
     */
    public static ArrayList<TodoItem> getTodoList (Date date) {
        ArrayList<TodoItem> todoItems = new ArrayList<>();
        for (BookItem bookItem : bookItems) {
            String Date = date.getYear() + "";
            if (date.getMonth() < 10) {
                Date += "-0" + (date.getMonth() + 1);
            } else {
                Date += "-" + (date.getMonth() + 1);
            }
            if (date.getDate() < 10) {
                Date += "-0" + date.getDate();
            } else {
                Date += "-" + date.getDate();
            }
            if (bookItem.getEndDate().equals(Date)) {
                todoItems.add(new TodoItem("还书", bookItem.getBookName(), "00:00", "23:59"));
            }
        }
        return todoItems;
    }
}
