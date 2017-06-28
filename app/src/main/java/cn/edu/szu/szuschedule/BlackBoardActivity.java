package cn.edu.szu.szuschedule;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.*;
import android.os.Bundle;
import android.view.*;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.edu.szu.szuschedule.adapter.HomeworkAdapter;
import cn.edu.szu.szuschedule.adapter.ViewPagerAdapter;
import cn.edu.szu.szuschedule.fragment.SubjectListFragment;
import cn.edu.szu.szuschedule.fragment.HomeworkListFragment;
import cn.edu.szu.szuschedule.object.Homework;
import cn.edu.szu.szuschedule.object.SubjectItem;
import cn.edu.szu.szuschedule.object.User;
import cn.edu.szu.szuschedule.service.BBService;
import cn.edu.szu.szuschedule.service.UserService;
import cn.edu.szu.szuschedule.util.LoadingUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static cn.edu.szu.szuschedule.util.DisplayUtil.setTranslucentStatus;

/**
 * Created by jazzyzhong on 2017/6/3.
 */
public class BlackBoardActivity extends AppCompatActivity implements HomeworkListFragment.OnCreateListener, HomeworkAdapter.OnClickListener {
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    LoadingUtil loadingUtil;
    ArrayList<SubjectItem> subjectItems;

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
        tabLayout.getTabAt(0).setText(R.string.bb_homework_list);
        tabLayout.getTabAt(1).setText(R.string.bb_subject_list);

        //获得科目内容
        loadingUtil = new LoadingUtil(this);
    }

    private void initViewPager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        HomeworkListFragment homeworkListFragment = new HomeworkListFragment();
        homeworkListFragment.setOnCreateListener(this);
        adapter.addFragment(homeworkListFragment);
        adapter.addFragment(new SubjectListFragment());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(int position, View view, Homework homework) {
        BlackBoardHomeworkInfoActivity.homework = homework;
        startActivity(new Intent(this, BlackBoardHomeworkInfoActivity.class));
    }

    @Override
    public void onCreate(final RecyclerView recyclerView) {
        final List<Homework> all = new ArrayList<>();
        final HomeworkAdapter adapter = new HomeworkAdapter(all);
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);
        BBService.getAllCourses(this)
                .flatMap(new Function<ArrayList<SubjectItem>, ObservableSource<SubjectItem>>() {
                    @Override
                    public ObservableSource<SubjectItem> apply(ArrayList<SubjectItem> subjectItems) throws Exception {
                        return Observable.fromIterable(subjectItems);
                    }
                })
                .flatMap(new Function<SubjectItem, ObservableSource<List<Homework>>>() {
                    @Override
                    public ObservableSource<List<Homework>> apply(SubjectItem subjectItem) throws Exception {
                        return BBService.getHomework(BlackBoardActivity.this, subjectItem);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Homework>>() {
                    @Override
                    public void accept(List<Homework> homework) throws Exception {
                        for (Homework homework1 : homework) {
                            if (!homework1.isFinished()) {
                                all.add(homework1);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }
}
