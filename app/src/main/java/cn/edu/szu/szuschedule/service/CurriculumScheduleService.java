package cn.edu.szu.szuschedule.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.edu.szu.szuschedule.object.Course;
import cn.edu.szu.szuschedule.object.TodoItem;
import cn.edu.szu.szuschedule.object.User;
import cn.edu.szu.szuschedule.util.CommonUtil;
import com.lzy.okgo.OkGo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenlin on 27/06/2017.
 */
public class CurriculumScheduleService {
    public static List<Course> courses;

    private static String currentTerm = "20162";
    private static String scheduleUrl = "http://pencilsky.cn:9090/api/curriculum/?stuNum=%s&term=%s"; // 课程信息

    private static Pattern classTimePattern = Pattern.compile("([^0-9]+)([0-9]+),([0-9]+)");

    public static Observable<List<Course>> getCourses(final Context context) {
        return Observable.create(new ObservableOnSubscribe<List<Course>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Course>> e) throws Exception {
                if (courses == null) {
                    courses = new ArrayList<>();
                    getLocalDatabaseData(context);

                    // 如果本地数据库没有数据，则从网络上获取
                    if (courses.size() == 0) {
                        String error = getCourseData(context);
                        if (error != null) {
                            e.onError(new Throwable(error));
                            return ;
                        }
                    }
                }
                e.onNext(courses);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 清除当前用户所有课程表信息
     * @param context
     * @return
     */
    public static void clearCourses(Context context) {
        courses = null;
        SQLiteDatabase db = DBHelper.getDB(context);
        String userId = String.valueOf(UserService.getCurrentUser().getId());
        Cursor cursor = db.rawQuery("select lesson.id from schedule inner join lesson on schedule.studentID = ? and lesson.id = schedule.lessonID", new String[]{userId});
        while (cursor.moveToNext()) {
            String lessonId = cursor.getString(cursor.getColumnIndex("id"));
            db.delete("schedule", "studentID = ? and lessonID = ?", new String[]{userId, lessonId});
            db.delete("lesson", "id=?", new String[]{lessonId});
        }
        cursor.close();
        db.close();
    }

    /**
     * 更改课程的上课地点
     * @param context
     * @param course
     */
    public static void updateVenue(Context context, Course course) {
        SQLiteDatabase db = DBHelper.getDB(context);
        ContentValues cv = new ContentValues();
        cv.put("location", course.getVenue());
        db.update("lesson", cv, "id=?", new String[]{String.valueOf(course.getId())});
    }

    /**
     * 获取某天的课程列表
     * @param date
     * @return
     */
    public static List<TodoItem> getTodoList(Date date) {
        String timeFormat = "第%d节课";
        List<TodoItem> todoItems = new ArrayList<>();
        if (courses == null) {
            return todoItems;
        }
        int day = date.getDay();
        for (Course course : courses) {
            if (course.getDay() == day) {
                todoItems.add(new TodoItem(course.getCourseName(), course.getVenue(), String.format(timeFormat, course.getBegin()), String.format(timeFormat, course.getEnd())));
            }
        }
        return todoItems;
    }

    /**
     * 从网络上获取课程表信息
     * 并保存到数据库中
     * @return 成功返回null 失败返回错误信息;
     */
    private static String getCourseData(Context context) {
        User user = UserService.getCurrentUser();
        try {
            Response response = OkGo.<String>get(String.format(scheduleUrl, user.getStuNum(), currentTerm))
                    .execute();
            JSONObject res = new JSONObject(response.body().string());
            if (res.getInt("code") != 10000) {
                return res.getString("error");
            }
            JSONObject data = res.getJSONObject("data");
            JSONArray coursesInfo = data.getJSONArray(currentTerm);
            if (coursesInfo != null) {
                int count = coursesInfo.length();
                SQLiteDatabase db = DBHelper.getDB(context);
                for (int i = 0; i < count; ++i) {
                    // 遍历所有课程
                    JSONObject courseInfo = coursesInfo.getJSONObject(i);
                    if (".".equals(courseInfo.getString("classTime"))) {
                        // 上课时间为.的课程为慕课课程，不需要添加到课程表中
                        continue;
                    }
                    String[] classTime = courseInfo.getString("classTime").split(";");
                    String[] venue = courseInfo.getString("venue").split(";");
                    // 遍历所有上课时间
                    for (int j = 0; j < classTime.length; ++j) {
                        Matcher matcher = classTimePattern.matcher(classTime[j]);
                        if (matcher.find()) {
                            Course course = new Course(
                                    -1,
                                    courseInfo.getString("courseName"),
                                    venue[j],
                                    CommonUtil.getDay(matcher.group(1)),
                                    Integer.valueOf(matcher.group(2)),
                                    Integer.valueOf(matcher.group(3)));
                            saveToDatabase(db, course);
                            courses.add(course);
                        }
                    }
                }
                db.close();
            }

            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "发生未知错误";
    }

    /**
     * 保存课程到数据库中
     * @param db
     * @param course
     */
    private static void saveToDatabase(SQLiteDatabase db, Course course) {
        // 保存到lesson表
        ContentValues cv = new ContentValues();
        cv.put("lessonName", course.getCourseName());
        cv.put("location", course.getVenue());
        cv.put("day", course.getDay());
        cv.put("begin", course.getBegin());
        cv.put("end", course.getEnd());
        db.insert("lesson", null, cv);
        // 获取最后插入的记录id
        Cursor cursor = db.rawQuery("select last_insert_rowid() from library", null);
        int lastId = 0;
        if (cursor.moveToFirst()) lastId = cursor.getInt(0);
        cursor.close();
        course.setId(lastId);

        // 保存到schedule表
        cv = new ContentValues();
        cv.put("studentID", UserService.getCurrentUser().getId());
        cv.put("lessonID", lastId);
        db.insert("schedule", null, cv);
    }

    /**
     * 从本地数据库获取相关信息
     * @param context
     */
    private static void getLocalDatabaseData(Context context) {
        SQLiteDatabase db = DBHelper.getDB(context);
        User user = UserService.getCurrentUser();
        Cursor cursor = db.rawQuery("SELECT lesson.* FROM schedule INNER JOIN lesson ON schedule.studentID = ? AND schedule.lessonID = lesson.id", new String[] {String.valueOf(user.getId())});
        int idIndex = cursor.getColumnIndex("id");
        int lessonNameIndex = cursor.getColumnIndex("lessonName");
        int locationIndex = cursor.getColumnIndex("location");
        int dayIndex = cursor.getColumnIndex("day");
        int beginIndex = cursor.getColumnIndex("begin");
        int endIndex = cursor.getColumnIndex("end");
        while (cursor.moveToNext()) {
            Course course = new Course(
                    cursor.getInt(idIndex),
                    cursor.getString(lessonNameIndex),
                    cursor.getString(locationIndex),
                    cursor.getInt(dayIndex),
                    cursor.getInt(beginIndex),
                    cursor.getInt(endIndex)
            );
            courses.add(course);
        }
        cursor.close();
        db.close();
    }

}
