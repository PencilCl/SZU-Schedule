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
import cn.edu.szu.szuschedule.adapter.TodoListAdapter;
import cn.edu.szu.szuschedule.object.TodoItem;

import java.util.ArrayList;

/**
 * Created by chenlin on 30/05/2017.
 */
public class ScheduleFragment extends Fragment {
    View view;
    RecyclerView todoList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_todo_list, null);
        todoList = (RecyclerView) view.findViewById(R.id.todoList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        todoList.setLayoutManager(layoutManager);

        ArrayList<TodoItem> todoItems = new ArrayList<>();
        todoItems.add(new TodoItem("计算机系统2", "实验一", "-", "23:59", true));
        todoItems.add(new TodoItem("软件工程", "教学楼A666", "8:30", "10:00"));

        todoList.setAdapter(new TodoListAdapter(todoItems));

        return view;
    }
}
