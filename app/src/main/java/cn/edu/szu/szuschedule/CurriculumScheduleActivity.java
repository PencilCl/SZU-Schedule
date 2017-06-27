package cn.edu.szu.szuschedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.edu.szu.szuschedule.dialog.CourseInfoDialog;
import cn.edu.szu.szuschedule.object.Course;
import cn.edu.szu.szuschedule.service.CurriculumScheduleService;
import cn.edu.szu.szuschedule.view.CurriculumSchedule;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import java.util.List;

import static cn.edu.szu.szuschedule.util.DisplayUtil.setTranslucentStatus;

/**
 * Created by chenlin on 01/06/2017.
 */
public class CurriculumScheduleActivity extends AppCompatActivity implements CurriculumSchedule.OnClickListener, CourseInfoDialog.OnSaveListener {
    @Bind(R.id.curriculumGrid)
    CurriculumSchedule curriculumSchedule;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curriculum_schedule);
        ButterKnife.bind(this);
        setTranslucentStatus(this);

        ImageButton button_back = (ImageButton) findViewById(R.id.sub_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        curriculumSchedule.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(CurriculumScheduleActivity.this, "正在获取最新课程表信息", Toast.LENGTH_SHORT).show();
                CurriculumScheduleService.clearCourses(CurriculumScheduleActivity.this);
                curriculumSchedule.removeAllCourses();
                getCourseInfo();
            }
        });

        getCourseInfo();
    }

    private void getCourseInfo() {
        CurriculumScheduleService.getCourses(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Course>>() {
                    @Override
                    public void accept(List<Course> courses) throws Exception {
                        for (int i = 0; i < courses.size(); ++i) {
                            curriculumSchedule.addCourse(courses.get(i));
                        }
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Toast.makeText(CurriculumScheduleActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    @Override
    public void onClick(View v, Course course) {
        new CourseInfoDialog(this, course).setOnSaveListener(this);
    }

    @Override
    public void onSave(Course course, String venue) {
        course.setVenue(venue);
        CurriculumScheduleService.updateVenue(this, course);
        curriculumSchedule.updateCourse(course);
    }
}
