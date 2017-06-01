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
import cn.edu.szu.szuschedule.view.SquareButton;

import java.util.ArrayList;

/**
 * Created by chenlin on 24/05/2017.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
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
        moduleItems.add(new ModuleItem(R.string.module_blackboard, R.drawable.button_bb_bg, this));
        moduleItems.add(new ModuleItem(R.string.module_book, R.drawable.button_book_bg, this));
        moduleItems.add(new ModuleItem(R.string.module_gobye, R.drawable.button_gobye_bg, this));
        moduleItems.add(new ModuleItem(R.string.module_curriculum, R.drawable.button_course_bg, this));

        moduleList.setAdapter(new ModuleListAdapter(moduleItems));

        return view;
    }

    @Override
    public void onClick(View v) {
        SquareButton view = (SquareButton) v;
        String moduleName = view.getText().toString();
        if (moduleName.equals(getResources().getString(R.string.module_blackboard))) {
            // click module blackboard

        } else if (moduleName.equals(getResources().getString(R.string.module_book))) {
            // click module book

        } else if (moduleName.equals(getResources().getString(R.string.module_gobye))) {
            // click module gobye

        } else if (moduleName.equals(getResources().getString(R.string.module_curriculum))) {
            // click module curriculum

        }
    }
}
