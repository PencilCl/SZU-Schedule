package cn.edu.szu.szuschedule.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.adapter.TodoListAdapter;
import cn.edu.szu.szuschedule.object.TodoItem;
import cn.edu.szu.szuschedule.service.BBService;
import cn.edu.szu.szuschedule.service.CurriculumScheduleService;
import cn.edu.szu.szuschedule.service.LibraryService;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chenlin on 30/05/2017.
 */
public class ScheduleFragment extends Fragment implements CalendarView.OnDateChangeListener {
    View view;
    RecyclerView todoList;
    CalendarView calendarView;
    Date date;

    ArrayList<TodoItem> todoItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_todo_list, null);
        todoList = (RecyclerView) view.findViewById(R.id.todoList);
        calendarView = (CalendarView) view.findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        todoList.setLayoutManager(layoutManager);

        date = new Date();
        getTodoList();

        todoList.setAdapter(new TodoListAdapter(todoItems));

        return view;
    }

    @Override
    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
        date = new Date(year, month, dayOfMonth);
        getTodoList();
    }

    private void getTodoList() {
        todoItems.clear();
        todoItems.addAll(CurriculumScheduleService.getTodoList(date));
        todoItems.addAll(LibraryService.getTodoList(date));
        todoItems.addAll(BBService.getTodoList(date));
    }
}
