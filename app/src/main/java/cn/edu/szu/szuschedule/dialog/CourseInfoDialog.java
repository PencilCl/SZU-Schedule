package cn.edu.szu.szuschedule.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.object.Course;
import cn.edu.szu.szuschedule.service.CurriculumScheduleService;

/**
 * Created by chenlin on 07/06/2017.
 */
public class CourseInfoDialog implements View.OnClickListener {
    private Dialog mDialog;
    private EditText venue;
    private Button cancel;
    private Button save;
    private Course course;
    private OnSaveListener onSaveListener;

    public void setOnSaveListener(OnSaveListener onSaveListener) {
        this.onSaveListener = onSaveListener;
    }

    public interface OnSaveListener {
        void onSave(Course course, String venue);
    }

    public CourseInfoDialog(Context context, Course course) {
        this.course = course;

        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_course_info, null);

        TextView courseName = (TextView) mView.findViewById(R.id.courseName);
        courseName.setText(course.getCourseName());
        venue = (EditText) mView.findViewById(R.id.venue);
        venue.setText(course.getVenue());
        cancel = (Button) mView.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        save = (Button) mView.findViewById(R.id.save);
        save.setOnClickListener(this);

        mDialog = new Dialog(context);
        mDialog.setContentView(mView);
        mDialog.show();

        // 设置dialog宽度占满屏幕
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; //设置宽度
        window.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                if (this.onSaveListener != null) {
                    this.onSaveListener.onSave(course, venue.getText().toString());
                }
            case R.id.cancel:
                mDialog.cancel();
                break;
            default:
        }
    }
}
