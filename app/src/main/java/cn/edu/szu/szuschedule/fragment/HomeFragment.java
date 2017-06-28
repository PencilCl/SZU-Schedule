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

import cn.edu.szu.szuschedule.BlackBoardActivity;
import cn.edu.szu.szuschedule.CurriculumScheduleActivity;
import cn.edu.szu.szuschedule.GobyeActivity;
import cn.edu.szu.szuschedule.ModuleActivity;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.adapter.ModuleListAdapter;
import cn.edu.szu.szuschedule.object.ModuleItem;
import cn.edu.szu.szuschedule.LibraryBooksActivity;
import cn.edu.szu.szuschedule.view.SquareButton;

/**
 * Created by chenlin on 24/05/2017.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    View view;
    RecyclerView moduleList;
    ModuleListAdapter moduleListAdapter;

    ModuleItem moduleBB;
    ModuleItem moduleLibrary;
    ModuleItem moduleGobye;
    ModuleItem moduleSchedule;

    public ArrayList<ModuleItem> moduleItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, null);

        moduleList = (RecyclerView) view.findViewById(R.id.moduleList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        moduleList.setLayoutManager(layoutManager);

        moduleBB = new ModuleItem(R.string.module_blackboard, R.drawable.button_bb_bg, this);
        moduleLibrary = new ModuleItem(R.string.module_library, R.drawable.button_library_bg, this);
        moduleGobye = new ModuleItem(R.string.module_gobye, R.drawable.button_gobye_bg, this);
        moduleSchedule = new ModuleItem(R.string.module_schedule, R.drawable.button_schedule_bg, this);

        moduleItems = new ArrayList<>();
        moduleItems.add(moduleBB);
        moduleItems.add(moduleLibrary);
        moduleItems.add(moduleGobye);
        moduleItems.add(moduleSchedule);

        moduleListAdapter = new ModuleListAdapter(moduleItems);
        moduleList.setAdapter(moduleListAdapter);

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
            startActivity(new Intent(getContext(), BlackBoardActivity.class));
        } else if (moduleName.equals(getResources().getString(R.string.module_library))) {
            // click module book
            startActivity(new Intent(getContext(), LibraryBooksActivity.class));

        } else if (moduleName.equals(getResources().getString(R.string.module_gobye))) {
            // click module gobye
            startActivity(new Intent(getContext(), GobyeActivity.class));
        } else if (moduleName.equals(getResources().getString(R.string.module_schedule))) {
            // click module curriculum
            startActivity(new Intent(getContext(), CurriculumScheduleActivity.class));
        }
    }

    private void intent_to_module() {
        Intent intent = new Intent(getActivity(), ModuleActivity.class);
        intent.putExtra("module_bb", moduleItems.indexOf(moduleBB) != -1);
        intent.putExtra("module_library", moduleItems.indexOf(moduleLibrary) != -1);
        intent.putExtra("module_gobye", moduleItems.indexOf(moduleGobye) != -1);
        intent.putExtra("module_schedule", moduleItems.indexOf(moduleSchedule) != -1);

        startActivityForResult(intent, ModuleActivity.requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ModuleActivity.requestCode && resultCode == ModuleActivity.requestCode) {
            Boolean is_checked = data.getBooleanExtra("bb_checked", true);
            setSquareVisibility(moduleBB, is_checked);

            is_checked = data.getBooleanExtra("library_checked", true);
            setSquareVisibility(moduleLibrary, is_checked);

            is_checked = data.getBooleanExtra("gobye_checked", true);
            setSquareVisibility(moduleGobye, is_checked);

            is_checked = data.getBooleanExtra("schedule_checked", true);
            setSquareVisibility(moduleSchedule, is_checked);
        }
    }

    private void setSquareVisibility(ModuleItem moduleItem, Boolean is_checked) {
        int pos = moduleItems.indexOf(moduleItem);
        if (is_checked && pos == -1) {
            moduleItems.add(moduleItem);
            moduleListAdapter.notifyItemInserted(moduleItems.size());
        } else if (!is_checked && pos != -1) {
            moduleItems.remove(moduleItem);
            moduleListAdapter.notifyItemRemoved(pos);
            moduleListAdapter.notifyItemRangeChanged(pos, moduleItems.size() - pos);
        }
    }
}
