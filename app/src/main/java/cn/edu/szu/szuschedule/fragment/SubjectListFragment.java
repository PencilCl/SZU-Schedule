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
import android.widget.Toast;

import cn.edu.szu.szuschedule.HomeworkListActivity;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.adapter.SubjectAdapter;
import cn.edu.szu.szuschedule.object.Homework;
import cn.edu.szu.szuschedule.object.SubjectItem;
import cn.edu.szu.szuschedule.service.BBService;
import cn.edu.szu.szuschedule.util.LoadingUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenlin on 07/06/2017.
 */
public class SubjectListFragment extends Fragment implements SubjectAdapter.OnClickListener, BBService.OnDataChangedListener {
    View view;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    SubjectAdapter adapter;
    List<SubjectItem> mSubjectItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subject_list, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.subject_recycle);
        LinearLayoutManager sub_list_layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(sub_list_layoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.course_Refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BBService.refreshSubject();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_green_light));

        BBService.addOnDataChangedListener(this);

        return view;
    }

    @Override
    public void onClick(int position, View view, SubjectItem subjectItem) {
        HomeworkListActivity.subjectItem = subjectItem;
        startActivity(new Intent(getContext(), HomeworkListActivity.class));
    }

    @Override
    public void onSubjectItemsChanged(List<SubjectItem> subjectItems) {
        if (mSubjectItems == null) {
            mSubjectItems = subjectItems;
            adapter = new SubjectAdapter(mSubjectItems);
            adapter.setOnClickListener(this);
            recyclerView.setAdapter(adapter);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onHomeworkChanged(List<Homework> homeworkList) {

    }

    @Override
    public void onDestroyView() {
        BBService.removeOnDataChangedListener(this);
        super.onDestroyView();
    }
}
