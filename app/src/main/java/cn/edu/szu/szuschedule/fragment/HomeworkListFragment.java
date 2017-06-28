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

/**
 * Created by chenlin on 07/06/2017.
 */
public class HomeworkListFragment extends Fragment {
    View view;
    RecyclerView homeworkList;
    private OnCreateListener onCreateListener;

    public interface OnCreateListener {
        void onCreate(RecyclerView recyclerView);
    }

    public void setOnCreateListener(OnCreateListener onCreateListener) {
        this.onCreateListener = onCreateListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_undo_homework_list, null);
        homeworkList = (RecyclerView) view.findViewById(R.id.homeworkList);
        LinearLayoutManager sub_list_layoutManager = new LinearLayoutManager(getContext());
        homeworkList.setLayoutManager(sub_list_layoutManager);

        if (this.onCreateListener != null) {
            this.onCreateListener.onCreate(homeworkList);
        }

        return view;
    }
}
