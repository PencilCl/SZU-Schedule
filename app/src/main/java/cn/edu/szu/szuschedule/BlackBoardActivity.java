package cn.edu.szu.szuschedule;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.*;
import android.os.Bundle;
import android.view.*;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.edu.szu.szuschedule.adapter.HomeworkAdapter;
import cn.edu.szu.szuschedule.adapter.ViewPagerAdapter;
import cn.edu.szu.szuschedule.fragment.SubjectListFragment;
import cn.edu.szu.szuschedule.fragment.HomeworkListFragment;
import cn.edu.szu.szuschedule.object.Homework;

import static cn.edu.szu.szuschedule.util.DisplayUtil.setTranslucentStatus;

/**
 * Created by jazzyzhong on 2017/6/3.
 */
public class BlackBoardActivity extends AppCompatActivity implements HomeworkAdapter.OnClickListener {
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    ViewPagerAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bb_index);
        ButterKnife.bind(this);
        setTranslucentStatus(this);

        ImageButton button_back = (ImageButton) findViewById(R.id.sub_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initViewPager();
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(R.string.bb_unfinished_homework_list);
        tabLayout.getTabAt(1).setText(R.string.bb_subject_list);
    }

    private void initViewPager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeworkListFragment());
        adapter.addFragment(new SubjectListFragment());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(int position, View view, Homework homework) {
        BlackBoardHomeworkInfoActivity.homework = homework;
        startActivity(new Intent(this, BlackBoardHomeworkInfoActivity.class));
    }
}
