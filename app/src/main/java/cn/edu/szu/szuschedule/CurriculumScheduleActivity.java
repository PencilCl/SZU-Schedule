package cn.edu.szu.szuschedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.edu.szu.szuschedule.view.CurriculumSchedule;

import static cn.edu.szu.szuschedule.util.DisplayUtil.setTranslucentStatus;

/**
 * Created by chenlin on 01/06/2017.
 */
public class CurriculumScheduleActivity extends AppCompatActivity {
    @Bind(R.id.curriculumGrid)
    CurriculumSchedule curriculumSchedule;

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

        curriculumSchedule.setCurrentDay(4);

        curriculumSchedule.addCourse("计算机系统(2)", "教学楼B506", 1, 1, 2);
        curriculumSchedule.addCourse("离散数学", "教学楼B311", 1, 3, 4);
        curriculumSchedule.addCourse("互联网编程", "教学楼C407", 1, 5, 6);
        curriculumSchedule.addCourse("互联网编程", "实验室D324", 1, 7, 8);

        curriculumSchedule.addCourse("操作系统", "教学楼C410", 2, 1, 2);
        curriculumSchedule.addCourse("操作系统", "实验室D241", 2, 3, 4);
        curriculumSchedule.addCourse("计算机论题", "教学楼C413", 2, 5, 6);
        curriculumSchedule.addCourse("软件工程", "实验室D326", 2, 7, 8);
        curriculumSchedule.addCourse("马克思主义基本原理", "文科楼H-06", 2, 9, 10);
        curriculumSchedule.addCourse("毛泽东思想和中国特色社会主义理论体系概论(2)", "师院A204", 2, 11, 12);

        curriculumSchedule.addCourse("离散数学", "教学楼B311", 3, 1, 2);
        curriculumSchedule.addCourse("计算机网络", "教学楼C414", 3, 5, 6);
        curriculumSchedule.addCourse("计算机网络", "实验室D325", 3, 7, 8);

        curriculumSchedule.addCourse("软件工程", "教学楼A207", 4, 3, 4);
        curriculumSchedule.addCourse("计算机系统(2)", "教学楼B506", 4, 7, 8);
    }
}
