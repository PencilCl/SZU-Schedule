package cn.edu.szu.szuschedule;

import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import java.util.*;
import android.annotation.*;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.*;
import android.os.Bundle;
import android.view.*;
import cn.edu.szu.szuschedule.adapter.SubjectAdapter;
import cn.edu.szu.szuschedule.object.SubjectItem;

import static cn.edu.szu.szuschedule.util.DisplayUtil.setTranslucentStatus;

/**
 * Created by jazzyzhong on 2017/6/3.
 */
public class BlackBoardActivity extends AppCompatActivity {
    public List<SubjectItem> subitem = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bb_index);
        setTranslucentStatus(this);

        ImageButton button_back = (ImageButton) findViewById(R.id.sub_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setinfo();
        RecyclerView recy = (RecyclerView)findViewById(R.id.subject_recycle);
        LinearLayoutManager sub_list_layoutManager = new LinearLayoutManager(this);
        recy.setLayoutManager(sub_list_layoutManager);
        SubjectAdapter subadpter = new SubjectAdapter(subitem);
        recy.setAdapter(subadpter);
    }

    private void setinfo(){

            SubjectItem sitem1 = new SubjectItem("软件工程", "有新动态");
            subitem.add(sitem1);
            SubjectItem sitem2 = new SubjectItem("计算机系统", "有新动态");
            subitem.add(sitem2);
    }

}
