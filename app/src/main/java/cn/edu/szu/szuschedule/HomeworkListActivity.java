package cn.edu.szu.szuschedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.edu.szu.szuschedule.adapter.HomeworkAdapter;
import cn.edu.szu.szuschedule.object.Homework;
import cn.edu.szu.szuschedule.object.SubjectItem;
import cn.edu.szu.szuschedule.service.BBService;
import cn.edu.szu.szuschedule.util.DisplayUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenlin on 07/06/2017.
 */
public class HomeworkListActivity extends AppCompatActivity implements HomeworkAdapter.OnClickListener, BBService.OnDataChangedListener {
    public static SubjectItem subjectItem;

    @Bind(R.id.homeworkList)
    RecyclerView homeworkList;

    List<Homework> mHomework = new ArrayList<>();
    HomeworkAdapter homeworkAdapter = new HomeworkAdapter(mHomework);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_list);
        ButterKnife.bind(this);
        DisplayUtil.setTranslucentStatus(this);

        ImageButton button_back = (ImageButton) findViewById(R.id.sub_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayoutManager sub_list_layoutManager = new LinearLayoutManager(this);
        homeworkList.setLayoutManager(sub_list_layoutManager);
        homeworkList.setAdapter(homeworkAdapter);
        homeworkAdapter.setOnClickListener(this);

        BBService.addOnDataChangedListener(this);
    }

    @Override
    public void onClick(int position, View view, Homework homework) {
        BlackBoardHomeworkInfoActivity.homework = homework;
        startActivity(new Intent(this, BlackBoardHomeworkInfoActivity.class));
    }

    @Override
    public void onSubjectItemsChanged(List<SubjectItem> subjectItems) {

    }

    @Override
    public void onHomeworkChanged(List<Homework> homeworkList) {
        for (Homework homework : homeworkList) {
            if (homework.getSubjectItem() == subjectItem && !mHomework.contains(homework)) {
                mHomework.add(homework);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                homeworkAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        BBService.removeOnDataChangedListener(this);
        super.onDestroy();
    }
}
