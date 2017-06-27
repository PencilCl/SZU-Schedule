package cn.edu.szu.szuschedule.service;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.*;
import cn.edu.szu.szuschedule.object.BookItem;
import cn.edu.szu.szuschedule.object.User;
import cn.edu.szu.szuschedule.util.SZUAuthenticationWebViewClient;
import com.lzy.okgo.OkGo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
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
     * 登录图书馆
     * 使用图书馆相关功能前应当调用此方法，确保完成了图书馆的登录
     * @param username 校园卡号
     * @param password 统一身份认证密码
     * @return 登录失败抛出异常，登录成功返回空字符串
     */
    public static Observable<String> loginLibrary(final String username, final String password) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                LibraryService libraryServiceService = getInstance();
                libraryServiceService.webView.setWebViewClient(new SZUAuthenticationWebViewClient(username, password, e, loginPageUrl, booksUrl));
                libraryServiceService.webView.loadUrl(booksUrl);
            }
        });
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
                    String error = getDataFromNetwork(context);
                    if (error == null) {
                        e.onNext(bookItems);
                    } else {
                        e.onError(new Throwable(error));
                    }
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

                getDataFromNetwork(context);// 从网络中获取中获取
               // System.out.println("更新图书信息");
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 从网络上获取借阅数据
     * 并保存到数据库中
     * @return 成功返回null 失败返回错误信息;
     */
    private static String getDataFromNetwork(Context context) {
        CookieManager cookieManager = CookieManager.getInstance();
        if(cookieManager.getCookie(booksUrl) == null) {
            return "登录信息失效";
        }
        try {


            okhttp3.Response response = OkGo.get(booksUrl)
                    .headers("Cookie", cookieManager.getCookie(booksUrl))
                    .execute();
            String html = response.body().string();
            Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(html);
            SQLiteDatabase db = DBHelper.getDB(context);
            //清空原来的内容
            bookItems.clear();
            db.execSQL("delete from library");
            while(matcher.find()) {
                BookItem book = new BookItem(-1, UserService.getCurrentUser(), matcher.group(2), matcher.group(3), matcher.group(1));
                ContentValues cv = new ContentValues();
                cv.put("studentID", book.getUser().getId());
                cv.put("bookName", book.getBook_Name());
                cv.put("startDate", book.getBorrow_Time());
                cv.put("endDate", book.getReturn_DeadLine());
                db.insert("library", null, cv);
                // 获取最后插入的记录id
                Cursor cursor = db.rawQuery("select last_insert_rowid() from library", null);
                int lastId = 0;
                if (cursor.moveToFirst()) lastId = cursor.getInt(0);
                cursor.close();
                book.setId(lastId);
                System.out.println("外网    "+book.getBook_Name()+"    "+book.getUserName()+"     "+book.getBorrow_Time()+"'''''''''''''''''''''");
                bookItems.add(book);
            }
            db.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "发生未知错误";
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
                    user,
                    cursor.getString(bookNameIndex),
                    cursor.getString(startDateIndex),
                    cursor.getString(endDateIndex)
            );
            System.out.println("本地    "+bookmark.getBook_Name()+"    "+bookmark.getUserName()+"     "+bookmark.getBorrow_Time()+"'''''''''''''''''''''");

            bookItems.add(bookmark);
        }
        cursor.close();
        db.close();
    }
}
