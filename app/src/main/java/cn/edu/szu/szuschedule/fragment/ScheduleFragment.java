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
import android.widget.Toast;

import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.adapter.TodoListAdapter;
import cn.edu.szu.szuschedule.object.TodoItem;
import cn.edu.szu.szuschedule.service.BBService;
import cn.edu.szu.szuschedule.service.CurriculumScheduleService;
import cn.edu.szu.szuschedule.service.LibraryService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

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
    TodoListAdapter adapter;
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
        adapter =  new TodoListAdapter(todoItems);
        todoList.setAdapter(adapter);

        date = new Date();
        date.setDate(date.getDate() + 1);
        getTodoList();

        return view;
    }

    @Override
    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
        date = new Date(year - 1900, month, dayOfMonth);
        getTodoList();
    }

    private void getTodoList() {
        todoItems.clear();
        CurriculumScheduleService.getTodoList(getActivity(), date)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<TodoItem>>() {
                    @Override
                    public void accept(ArrayList<TodoItem> todoItemArrayList) throws Exception {
                        todoItems.addAll(todoItemArrayList);
                        adapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        todoItems.addAll(LibraryService.getTodoList(date));
        todoItems.addAll(BBService.getTodoList(date));
        adapter.notifyDataSetChanged();
    }
}
