package cn.edu.szu.szuschedule;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.fragment.HomeFragment;

public class ModuleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        get_intent_data();

        Button button_back = (Button) findViewById(R.id.module_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_back();
                finish();
            }
        });
    }

    private void get_intent_data(){
        Intent intent = getIntent();
        int module_visibility[] = intent.getIntArrayExtra("module_visibility");
        Switch switch1 = (Switch)findViewById(R.id.switch_bb);
        switch_setvisibility(module_visibility[0],switch1);
        switch1 = (Switch)findViewById(R.id.switch_library);
        switch_setvisibility(module_visibility[1],switch1);
        switch1 = (Switch)findViewById(R.id.switch_gobye);
        switch_setvisibility(module_visibility[2],switch1);
        switch1 = (Switch)findViewById(R.id.switch_schedule);
        switch_setvisibility(module_visibility[3],switch1);

    }

    private void switch_setvisibility(int visibility,Switch s){
        switch (visibility){
            case 0:
                s.setChecked(true);
                break;
            default:
                s.setChecked(false);
                break;
        }
    }

    private void intent_back(){
        Intent intent = new Intent();
        Switch switch1 = (Switch) findViewById(R.id.switch_bb);
        boolean is_checked = switch1.isChecked();
        intent.putExtra("bb_checked",is_checked);
        switch1 = (Switch)findViewById(R.id.switch_library);
        is_checked = switch1.isChecked();
        intent.putExtra("library_checked",is_checked);
        switch1 = (Switch)findViewById(R.id.switch_gobye);
        is_checked = switch1.isChecked();
        intent.putExtra("gobye_checked",is_checked);
        switch1 = (Switch)findViewById(R.id.switch_schedule);
        is_checked = switch1.isChecked();
        intent.putExtra("schedule_checked",is_checked);
        setResult(1,intent);
    }
    @Override
    public void onBackPressed() {
        intent_back();
        finish();
    }
}
