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
import cn.edu.szu.szuschedule.HomeworkListActivity;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.adapter.SubjectAdapter;
import cn.edu.szu.szuschedule.object.SubjectItem;

import java.util.ArrayList;

/**
 * Created by chenlin on 07/06/2017.
 */
public class SubjectListFragment extends Fragment {
    View view;
    RecyclerView subjectList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subject_list, null);
        subjectList = (RecyclerView) view.findViewById(R.id.subject_recycle);
        LinearLayoutManager sub_list_layoutManager = new LinearLayoutManager(getContext());
        subjectList.setLayoutManager(sub_list_layoutManager);

        ArrayList<SubjectItem> subjectItems = new ArrayList<>();
//        subjectItems.add(new SubjectItem("软件工程", "有新动态", this));
//        subjectItems.add(new SubjectItem("计算机系统", "有新动态", this));

        SubjectAdapter subadpter = new SubjectAdapter(subjectItems);
        subjectList.setAdapter(subadpter);

        return view;
    }
}
