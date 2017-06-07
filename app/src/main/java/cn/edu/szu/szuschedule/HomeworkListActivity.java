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
import cn.edu.szu.szuschedule.object.HomeworkItem;
import cn.edu.szu.szuschedule.util.DisplayUtil;

import java.util.ArrayList;

/**
 * Created by chenlin on 07/06/2017.
 */
public class HomeworkListActivity extends AppCompatActivity implements HomeworkItem.OnClickListener {
    @Bind(R.id.homeworkList)
    RecyclerView homeworkList;

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

        ArrayList<HomeworkItem> homeworkItems = new ArrayList<>();
        homeworkItems.add(new HomeworkItem("软件工程", "实验1 软件界面设计", "2017年3月27日 下午11时30分00秒", 100, this));
        homeworkItems.add(new HomeworkItem("软件工程", "实验2 数据库建模", "2017年4月4日 下午10时00分00秒", this));

        homeworkList.setAdapter(new HomeworkAdapter(homeworkItems));
    }

    @Override
    public void onClick(View view, HomeworkItem homeworkItem) {
        startActivity(new Intent(this, BlackBoardHomeworkInfoActivity.class));
    }
}
