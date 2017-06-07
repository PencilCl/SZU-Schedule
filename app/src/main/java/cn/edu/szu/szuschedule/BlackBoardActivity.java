package cn.edu.szu.szuschedule;

import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import java.util.*;
import android.annotation.*;
import android.widget.*;
import android.os.Bundle;
import android.view.*;

import static cn.edu.szu.szuschedule.util.DisplayUtil.setTranslucentStatus;

/**
 * Created by jazzyzhong on 2017/6/3.
 */
public class BlackBoardActivity extends AppCompatActivity {
    ListView sub_list;
    //private List<info> listinfo = new ArrayList<info>();
    Button[] btns = new Button[4];
    Toolbar toolbar1;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bb_subject_list);
        setTranslucentStatus(this);

        ImageButton button_back = (ImageButton) findViewById(R.id.sub_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*toolbar1 = (Toolbar)findViewById(R.id.bb_index_toolbar);
        sub_list = (ListView)this.findViewById(R.id.sublist);//建立联系

        btns[0] = (Button)findViewById(R.id.subject1);
        btns[1] = (Button)findViewById(R.id.subject2);
        btns[2] = (Button)findViewById(R.id.subject3);
        btns[3] = (Button)findViewById(R.id.subject4);
        Button button_back = (Button) findViewById(R.id.sub_back);
        button_back.setOnClickListener(newm  View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
*/
        /*Button button_back = (Button) findViewById(R.id.module_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_back();
                finish();
            }
        });*/

       /* ArrayAdapter<Button> btn_array = new ArrayAdapter<Button>(this,R.id.sublist,btns);
        sub_list.setAdapter(btn_array);*/
        //sub_list.setAdapter(new ListAdapter());
    }


}
