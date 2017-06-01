package cn.edu.szu.szuschedule.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.adapter.ModuleListAdapter;
import cn.edu.szu.szuschedule.object.ModuleItem;

import java.util.ArrayList;

/**
 * Created by chenlin on 24/05/2017.
 */
public class HomeFragment extends Fragment {
    View view;
    RecyclerView moduleList;
    public ArrayList<ModuleItem> moduleItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, null);

        moduleList = (RecyclerView) view.findViewById(R.id.moduleList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        moduleList.setLayoutManager(layoutManager);

        moduleItems = new ArrayList<>();
        moduleItems.add(new ModuleItem(R.string.module_blackboard, R.drawable.button_bb_bg));
        moduleItems.add(new ModuleItem(R.string.module_book, R.drawable.button_book_bg));
        moduleItems.add(new ModuleItem(R.string.module_gobye, R.drawable.button_gobye_bg));
        moduleItems.add(new ModuleItem(R.string.module_course, R.drawable.button_course_bg));
        moduleItems.add(new ModuleItem(R.string.module_gobye, R.drawable.button_gobye_bg));
        moduleItems.add(new ModuleItem(R.string.module_course, R.drawable.button_course_bg));

        moduleList.setAdapter(new ModuleListAdapter(moduleItems));

        return view;
    }
}
