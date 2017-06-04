package cn.edu.szu.szuschedule.view;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by chenlin on 04/06/2017.
 */
public class CurriculumSchedule extends GridLayout implements View.OnClickListener {
    static int currentDayColor = -6752769; // Color.parseColor("#98F5FF")
    static int[] bgColorArray = {
            -1644806, // Color.parseColor("#E6E6FA"),
            -983056, // Color.parseColor("#F0FFF0"),
            -983041, // Color.parseColor("#F0FFFF"),
            -3851, // Color.parseColor("#FFF0F5"),
            -4133, // Color.parseColor("#FFEFDB"),
            -4198401,
            -2031617,
            -7681
    };

    public CurriculumSchedule(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        removeAllViews();
        init();
    }

    public CurriculumSchedule(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CurriculumSchedule(Context context) {
        this(context, null);
    }

    /**
     * 添加课程到课程表中
     * @param courseName 课程名
     * @param venue 上课地点
     * @param day 星期
     * @param begin 开始节数
     * @param end 结束节数
     */
    public void addCourse(String courseName, String venue, int day, int begin, int end) {
        if (day < 1 || day > 5) return ; // 检查星期
        if (begin > end || begin < 1 || end > 12) return ; // 检查上课时间

        TextView textView = new TextView(getContext());
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams(getContext(), null);
        lp.columnSpec = GridLayout.spec(day, 1.0f);
        lp.rowSpec = GridLayout.spec(begin, end - begin + 1, 1.0f);
        textView.setLayoutParams(lp);
        textView.setText(courseName + "\n" + venue);
        textView.setPadding(5, 5, 5, 5);
        textView.setMaxWidth(((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() / 7);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundColor(bgColorArray[strToInt(courseName) % bgColorArray.length]);
        textView.setOnClickListener(this);
        addView(textView);
    }

    @Override
    public void onClick(View v) {
        TextView textView = (TextView) v;
        System.out.println(textView.getText());
    }

    /**
     * 设置今天为周几
     */
    public void setCurrentDay(int day) {
        View view = getChildAt(day);
        if (view != null) {
            view.setBackgroundColor(currentDayColor);
        }
    }

    /**
     * 初始化课程表
     */
    private void init() {
        // 六列(周一到周五)十三行(1-12节)，
        setColumnCount(6);
        setRowCount(13);

        TextView textView = new TextView(getContext());
        textView.setText("");
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams(getContext(), null);
        lp.columnSpec = GridLayout.spec(0, .5f);
        lp.rowSpec = GridLayout.spec(0, 1.0f);
        textView.setLayoutParams(lp);
        addView(textView);

        // 第一行添加周一-周五
        for (int i = 1; i < 6; ++i) {
            textView = new TextView(getContext());
            textView.setText(numToUpper(i));
            textView.setGravity(Gravity.CENTER);
            lp = new GridLayout.LayoutParams(getContext(), null);
            lp.columnSpec = GridLayout.spec(i, 1.0f);
            lp.rowSpec = GridLayout.spec(0, 1.0f);
            textView.setLayoutParams(lp);
            addView(textView, i);
        }
        // 第一列添加1-12节
        for (int i = 1; i < 13; ++i) {
            textView = new TextView(getContext());
            textView.setText(String.valueOf(i));
            textView.setGravity(Gravity.CENTER);
            lp = new GridLayout.LayoutParams(getContext(), null);
            lp.columnSpec = GridLayout.spec(0, .5f);
            lp.rowSpec = GridLayout.spec(i, 1.0f);
            textView.setLayoutParams(lp);
            addView(textView);
        }
    }

    /**
     * 1->周一
     * @return
     */
    private static String numToUpper(int num) {
        String res;
        switch (num) {
            case 1:
                res = "周一";
                break;
            case 2:
                res = "周二";
                break;
            case 3:
                res = "周三";
                break;
            case 4:
                res = "周四";
                break;
            case 5:
                res = "周五";
                break;
            default:
                res = "周〇";
        }
        return res;
    }

    /**
     * 将字符串转换为int
     * @param str
     * @return
     */
    private static int strToInt(String str) {
        int res = 0;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes());
            for (int i = 0; i < bytes.length; ++i) {
                res += bytes[i];
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return Math.abs(res);
    }
}
