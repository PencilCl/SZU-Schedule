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

    private WebView webView;

    private static ArrayList<BookItem> bookItems;

    public static LibraryService getInstance() {
        return LibraryServiceHolder.holder;
    }

    private static class LibraryServiceHolder {
        private static LibraryService holder = new LibraryService();
    }

    public void init(Application app) {
        // 初始化WebView
        this.webView = new WebView(app);
        initWebView(this.webView);
    }

    /**
     * 获取当前用户借阅图书列表
     * @return 失败抛出异常，成功返回图书列表
     */
    public static Observable<ArrayList<BookItem>> getBorrowedBooks(final Context context) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<BookItem>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<BookItem>> e) throws Exception {
                if (bookItems == null) {
                    bookItems = new ArrayList<>();
                } else {
                    // 如果bookItems已存在，则直接返回
                    e.onNext(bookItems);
                    return ;
                }

                getLocalDatabaseData(context);// 从本地数据库中获取
                // 如果本地数据为空，则从网络中获取
                if (bookItems.size() == 0) {
                    getDataFromNetwork(context, e);
                } else {
                    e.onNext(bookItems);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 通过图书馆（外网）获取当前用户借阅图书列表
     * @return 失败抛出异常，成功返回图书列表
     */
    public static Observable<ArrayList<BookItem>> updateBorrowedBooks(final Context context) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<BookItem>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<BookItem>> e) throws Exception {
                if (bookItems == null) {
                    bookItems = new ArrayList<>();
                }

                getDataFromNetwork(context, e);// 从网络中获取
                e.onNext(bookItems);
               // System.out.println("更新图书信息");
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 获取指定日期需要归还的书本
     * @param date
     * @return
     */
    public static List<TodoItem> getTodoList(Date date) {
        List<TodoItem> todoItems = new ArrayList<>();
        if (bookItems == null) {
            return todoItems;
        }
        for (BookItem bookItem : bookItems) {
            if (bookItem.getEndDate().equals(date.toString())) {
                todoItems.add(new TodoItem("还书", bookItem.getBookName(), "00:00", "23:59"));
            }
        }
        return todoItems;
    }

    /**
     * 从网络上获取借阅数据
     * 并保存到数据库中
     * @return 成功返回null 失败返回错误信息;
     */
    private static void getDataFromNetwork(final Context context, final ObservableEmitter<ArrayList<BookItem>> e) {
        final User user = UserService.getCurrentUser();
        loginLibrary(user.getAccount(), user.getPassword())
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        CookieManager cookieManager = CookieManager.getInstance();
                        return OkGo.<String>get(booksUrl)
                                .headers("Cookie", cookieManager.getCookie(booksUrl))
                                .converter(new StringConvert())
                                .adapt(new ObservableBody<String>());
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String html) throws Exception {
                        try {
                            Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(html);
                            SQLiteDatabase db = DBHelper.getDB(context);
                            //清空原来的内容
                            bookItems.clear();
                            db.delete("library", "studentID = ?", new String[]{String.valueOf(user.getId())});
                            while(matcher.find()) {
                                BookItem book = new BookItem(-1, matcher.group(2), matcher.group(3), matcher.group(1));
                                ContentValues cv = new ContentValues();
                                cv.put("studentID", user.getId());
                                cv.put("bookName", book.getBookName());
                                cv.put("startDate", book.getStartDate());
                                cv.put("endDate", book.getEndDate());
                                db.insert("library", null, cv);
                                // 获取最后插入的记录id
                                Cursor cursor = db.rawQuery("select last_insert_rowid() from library", null);
                                int lastId = 0;
                                if (cursor.moveToFirst()) lastId = cursor.getInt(0);
                                cursor.close();
                                book.setId(lastId);
                                System.out.println("外网    "+book.getBookName()+"     "+book.getEndDate()+"'''''''''''''''''''''");
                                bookItems.add(book);
                            }
                            db.close();
                            e.onNext(bookItems);
                            return ;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        e.onError(new Throwable("发生未知错误"));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        e.onError(throwable);
                    }
                });
    }

    /**
     * 从本地数据库加载数据
     * @param context
     */
    private static void getLocalDatabaseData(Context context) {
        SQLiteDatabase db = DBHelper.getDB(context);
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
            System.out.println("本地    "+bookmark.getBookName()+"     "+bookmark.getEndDate()+"'''''''''''''''''''''");

            bookItems.add(bookmark);
        }
        cursor.close();
        db.close();
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
                LibraryService libraryServiceService = getInstance();
                libraryServiceService.webView.setWebViewClient(new SZUAuthenticationWebViewClient(username, password, e, loginPageUrl, booksUrl));
                libraryServiceService.webView.loadUrl(booksUrl);
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
}
