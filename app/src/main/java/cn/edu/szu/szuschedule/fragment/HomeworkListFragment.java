package cn.edu.szu.szuschedule.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.edu.szu.szuschedule.BlackBoardHomeworkInfoActivity;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.adapter.HomeworkAdapter;
import cn.edu.szu.szuschedule.object.Homework;

import java.util.ArrayList;

/**
 * Created by chenlin on 07/06/2017.
 */
public class HomeworkListFragment extends Fragment implements Homework.OnClickListener {
    View view;
    RecyclerView homeworkList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_undo_homework_list, null);
        homeworkList = (RecyclerView) view.findViewById(R.id.homeworkList);
        LinearLayoutManager sub_list_layoutManager = new LinearLayoutManager(getContext());
        homeworkList.setLayoutManager(sub_list_layoutManager);

        ArrayList<Homework> homeworkItems = new ArrayList<>();
        homeworkItems.add(new Homework("软件工程", "实验1 软件界面设计", "2017年3月27日 下午11时30分00秒", this));
        homeworkItems.add(new Homework("软件工程", "实验2 数据库建模", "2017年4月4日 下午10时00分00秒", this));

        homeworkList.setAdapter(new HomeworkAdapter(homeworkItems));

        return view;
    }

    @Override
    public void onClick(View view, Homework homeworkItem) {
        startActivity(new Intent(getContext(), BlackBoardHomeworkInfoActivity.class));
    }

}
