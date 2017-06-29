package cn.edu.szu.szuschedule.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.edu.szu.szuschedule.BlackBoardHomeworkInfoActivity;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.adapter.HomeworkAdapter;
import cn.edu.szu.szuschedule.object.Homework;
import cn.edu.szu.szuschedule.object.SubjectItem;
import cn.edu.szu.szuschedule.service.BBService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenlin on 07/06/2017.
 */
public class HomeworkListFragment extends Fragment implements BBService.OnDataChangedListener, HomeworkAdapter.OnClickListener {
    View view;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Homework> mHomework = new ArrayList<>();
    HomeworkAdapter adapter = new HomeworkAdapter(mHomework);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_undo_homework_list, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.homeworkList);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHomework.clear();
                notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(true);
                BBService.refreshAllHomework(); // 刷新所有课程列表
            }
        });

        LinearLayoutManager sub_list_layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(sub_list_layoutManager);
        BBService.addOnDataChangedListener(this);
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onSubjectItemsChanged(List<SubjectItem> subjectItems) {

    }

    @Override
    public void onHomeworkChanged(List<Homework> homeworkList) {
        mHomework.clear();
        for (Homework homework : homeworkList) {
            if (!homework.isFinished()) {
                mHomework.add(homework);
            }
        }
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(int position, View view, Homework homework) {
        BlackBoardHomeworkInfoActivity.homework = homework;
        startActivity(new Intent(getActivity(), BlackBoardHomeworkInfoActivity.class));
    }

    @Override
    public void onDestroyView() {
        BBService.removeOnDataChangedListener(this);
        super.onDestroyView();
    }
}
