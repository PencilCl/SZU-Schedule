package cn.edu.szu.szuschedule.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.edu.szu.szuschedule.object.User;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chenlin on 26/06/2017.
 */
public class UserService {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        UserService.currentUser = currentUser;
    }

    /**
     * 保存登录的用户信息
     * @param context
     * @param user
     * @return 返回保存结果
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
                    cv.put("name", "");
                    cv.put("sex", "无");
                    db.insert("student", null, cv);
                    // 获取最后插入的记录id
                    cursor = db.rawQuery("select last_insert_rowid() from student", null);
                    if (cursor.moveToFirst()) {
                        user.setId(cursor.getInt(0));
                    } else {
                        db.close();
                        cursor.close();
                        e.onNext(false);
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
                editor.commit();

                currentUser = user; // 设置当前用户为登录用户
                e.onNext(true);
            }
        }).subscribeOn(Schedulers.io());
    }
}
