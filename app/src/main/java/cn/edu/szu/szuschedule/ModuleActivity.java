package cn.edu.szu.szuschedule;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import butterknife.Bind;
import butterknife.ButterKnife;

import static cn.edu.szu.szuschedule.util.DisplayUtil.setTranslucentStatus;

public class ModuleActivity extends AppCompatActivity {
    public static int requestCode = 155;

    @Bind(R.id.switch_bb)
    Switch switchBB;
    @Bind(R.id.switch_gobye)
    Switch switchGobye;
    @Bind(R.id.switch_library)
    Switch switchLibrary;
    @Bind(R.id.switch_schedule)
    Switch switchSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);
        ButterKnife.bind(this);
        setTranslucentStatus(this);

        get_intent_data();

        ImageButton button_back = (ImageButton) findViewById(R.id.sub_back);
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
        switchBB.setChecked(intent.getBooleanExtra("module_bb", false));
        switchLibrary.setChecked(intent.getBooleanExtra("module_library", false));
        switchGobye.setChecked(intent.getBooleanExtra("module_gobye", false));
        switchSchedule.setChecked(intent.getBooleanExtra("module_schedule", false));
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
        setResult(requestCode,intent);
    }
    @Override
    public void onBackPressed() {
        intent_back();
        finish();
    }
}
