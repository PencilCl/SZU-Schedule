package cn.edu.szu.szuschedule.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.edu.szu.szuschedule.object.User;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okrx2.adapter.ObservableBody;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import org.json.JSONObject;

/**
 * Created by chenlin on 26/06/2017.
 */
public class UserService {
    private static String stuInfoUrl = "http://pencilsky.cn:9090/api/curriculum/student/?stuNum=%s";

    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        UserService.currentUser = currentUser;
    }

    /**
     * 从服务器获取指定用户的所有信息
     * @param user
     * @return
     */
    public static Observable<User> getUserInfo(final User user) {
        return OkGo.<String>get(String.format(stuInfoUrl, user.getStuNum()))
                .converter(new StringConvert())
                .adapt(new ObservableBody<String>())
                .map(new Function<String, User>() {
                    @Override
                    public User apply(String s) throws Exception {
                        JSONObject res = new JSONObject(s);
                        if (res.getInt("code") != 10000) {
                            return null;
                        }
                        JSONObject info = res.getJSONObject("data");
                        user.setName(info.getString("name"));
                        user.setSex(info.getString("sex"));
                        return user;
                    }
                });
    }

    /**
     * 保存登录的用户信息
     * @param context
     * @param user
     * @return
     */
    public static Observable<Boolean> saveUserInfo(final Context context, final User user) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                SQLiteDatabase db = DBHelper.getDB(context);

                // 判断用户信息是否已存在与数据库中
                Cursor cursor = db.rawQuery("select * from student where account=?", new String[]{user.getAccount()});
                if (cursor.moveToFirst()) {
                    // 更新用户信息
                    ContentValues cv = new ContentValues();
                    cv.put("sex", user.getSex());
                    cv.put("password", user.getPassword());
                    cv.put("stuNum", user.getStuNum());
                    db.update("student", cv, "id=?", new String[]{String.valueOf(user.getId())});
                    user.setId(cursor.getInt(cursor.getColumnIndex("id")));
                } else {
                    // 将信息存储到数据库当中
                    ContentValues cv = new ContentValues();
                    cv.put("account", user.getAccount());
                    cv.put("stuNum", user.getStuNum());
                    cv.put("password", user.getPassword());
                    cv.put("name", user.getName());
                    cv.put("sex", user.getSex());
                    db.insert("student", null, cv);
                    // 获取最后插入的记录id
                    cursor = db.rawQuery("select last_insert_rowid() from student", null);
                    if (cursor.moveToFirst()) {
                        user.setId(cursor.getInt(0));
                    } else {
                        db.close();
                        cursor.close();
                        e.onError(new Throwable("保存用户信息失败"));
                        return ;
                    }
                }
                db.close();
                cursor.close();

                // 在SharedPreferences中保存当前登录用户信息
                SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("id", user.getId());
                editor.putString("account", user.getAccount());
                editor.putString("stuNum", user.getStuNum());
                editor.putString("password", user.getPassword());
                editor.putString("sex", user.getSex());
                editor.putString("name", user.getName());
                editor.commit();

                currentUser = user; // 设置当前用户为登录用户
                e.onNext(true);
            }
        }).subscribeOn(Schedulers.io());
    }
}
