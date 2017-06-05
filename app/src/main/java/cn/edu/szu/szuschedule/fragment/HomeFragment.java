package cn.edu.szu.szuschedule.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import cn.edu.szu.szuschedule.CurriculumScheduleActivity;
import cn.edu.szu.szuschedule.GobyeActivity;
import cn.edu.szu.szuschedule.ModuleActivity;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.adapter.ModuleListAdapter;
import cn.edu.szu.szuschedule.object.ModuleItem;
import cn.edu.szu.szuschedule.view.SquareButton;

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

        Button module_control_button = (Button) view.findViewById(R.id.moduleController);
        module_control_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_to_module();
            }
        });

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
            startActivity(new Intent(getContext(), GobyeActivity.class));
        } else if (moduleName.equals(getResources().getString(R.string.module_curriculum))) {
            // click module curriculum
            startActivity(new Intent(getContext(), CurriculumScheduleActivity.class));
        }
    }

    private void intent_to_module() {
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.moduleList);
        Intent intent = new Intent(getActivity(), ModuleActivity.class);
        int[] is_visible = new int[4];
        RecyclerView.LayoutManager lm = rv.getLayoutManager();
        View view = lm.findViewByPosition(0);
        SquareButton sb = (SquareButton) view.findViewById(R.id.module);
        is_visible[0] = sb.getVisibility();
        view = lm.findViewByPosition(1);
        sb = (SquareButton) view.findViewById(R.id.module);
        is_visible[1] = sb.getVisibility();
        view = lm.findViewByPosition(2);
        sb = (SquareButton) view.findViewById(R.id.module);
        is_visible[2] = sb.getVisibility();
        view = lm.findViewByPosition(3);
        sb = (SquareButton) view.findViewById(R.id.module);
        is_visible[3] = sb.getVisibility();
        intent.putExtra("module_visibility", is_visible);

        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            RecyclerView rv = (RecyclerView) view.findViewById(R.id.moduleList);
            RecyclerView.LayoutManager lm = rv.getLayoutManager();
            View view = lm.findViewByPosition(0);
            SquareButton sb = (SquareButton) view.findViewById(R.id.module);
            Boolean is_checked = data.getBooleanExtra("bb_checked", true);
            set_Square_visibility(sb, is_checked);

            view = lm.findViewByPosition(1);
            sb = (SquareButton) view.findViewById(R.id.module);
            is_checked = data.getBooleanExtra("library_checked", true);
            set_Square_visibility(sb, is_checked);

            view = lm.findViewByPosition(2);
            sb = (SquareButton) view.findViewById(R.id.module);
            is_checked = data.getBooleanExtra("gobye_checked", true);
            set_Square_visibility(sb, is_checked);

            view = lm.findViewByPosition(3);
            sb = (SquareButton) view.findViewById(R.id.module);
            is_checked = data.getBooleanExtra("schedule_checked", true);
            set_Square_visibility(sb, is_checked);
        }
    }

    private void set_Square_visibility(SquareButton sb, Boolean is_checked) {
        if (is_checked) {
            sb.setVisibility(View.VISIBLE);
        } else {
            sb.setVisibility(View.INVISIBLE);
        }
    }
}
