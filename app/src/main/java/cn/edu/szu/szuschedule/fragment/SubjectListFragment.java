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
import cn.edu.szu.szuschedule.object.SubjectItem;
import cn.edu.szu.szuschedule.object.User;
import cn.edu.szu.szuschedule.service.BBService;
import cn.edu.szu.szuschedule.service.UserService;
import cn.edu.szu.szuschedule.util.LoadingUtil;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import java.util.ArrayList;

/**
 * Created by chenlin on 07/06/2017.
 */
public class SubjectListFragment extends Fragment implements SubjectAdapter.OnClickListener {
    View view;
    RecyclerView subjectList;
    ArrayList<SubjectItem> subjectItems;
    LoadingUtil loadingUtil;
    SwipeRefreshLayout course_Refresh;
    SubjectAdapter subadpter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subject_list, null);
        subjectList = (RecyclerView) view.findViewById(R.id.subject_recycle);
        LinearLayoutManager sub_list_layoutManager = new LinearLayoutManager(getContext());
        subjectList.setLayoutManager(sub_list_layoutManager);

        course_Refresh = (SwipeRefreshLayout) view.findViewById(R.id.course_Refresh);

        course_Refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(),"正在刷新",Toast.LENGTH_SHORT).show();
                getCourses(1);
                subadpter.notifyDataSetChanged();
                course_Refresh.setRefreshing(false);

            }
        });
        course_Refresh.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_green_light));
        subjectItems = new ArrayList<>();
        loadingUtil = new LoadingUtil(getActivity());
        getCourses(0);

        return view;
    }

    @Override
    public void onClick(int position, View view, SubjectItem subjectItem) {
        HomeworkListActivity.subjectItem = subjectItem;
        startActivity(new Intent(getContext(), HomeworkListActivity.class));
    }

    public  void getCourses(final int i) {
       // loadingUtil.showLoading();
        User user = UserService.getCurrentUser();
        BBService.loginBB(user.getAccount(), user.getPassword())
                .flatMap(new Function<String, ObservableSource<ArrayList<SubjectItem>>>() {
                    @Override
                    public ObservableSource<ArrayList<SubjectItem>> apply(String s) throws Exception {
                        if(i == 0)
                            return BBService.getAllCourses(getActivity());
                        else
                            return BBService.updateAllCourses(getActivity());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<SubjectItem>>() {
                    @Override
                    public void accept(@NonNull ArrayList<SubjectItem> SubjectItems) throws Exception {
                        subjectItems = SubjectItems;
                        subadpter = new SubjectAdapter(subjectItems);
                        subadpter.setOnClickListener(SubjectListFragment.this);
                        subjectList.setAdapter(subadpter);
                        //loadingUtil.hideLoading();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                       //  loadingUtil.hideLoading();
                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
