package cn.edu.szu.szuschedule.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.adapter.HomeworkAdapter;
import cn.edu.szu.szuschedule.object.Homework;
import cn.edu.szu.szuschedule.object.SubjectItem;
import cn.edu.szu.szuschedule.service.BBService;

import java.util.List;

/**
 * Created by chenlin on 07/06/2017.
 */
public class HomeworkListFragment extends Fragment implements BBService.OnDataChangedListener {
    View view;
    RecyclerView recyclerView;
    List<Homework> homework;
    HomeworkAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_undo_homework_list, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.homeworkList);
        LinearLayoutManager sub_list_layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(sub_list_layoutManager);
        BBService.addOnDataChangedListener(this);

        return view;
    }

    @Override
    public void onSubjectItemsChanged(List<SubjectItem> subjectItems) {

    }

    @Override
    public void onHomeworkChanged(List<Homework> homeworkList) {
        if (homework == null) {
            homework = homeworkList;
            adapter = new HomeworkAdapter(homeworkList);
            recyclerView.setAdapter(adapter);
        }
        System.out.println(homeworkList.size());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        BBService.removeOnDataChangedListener(this);
        super.onDestroyView();
    }
}
