package cn.edu.szu.szuschedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

import static cn.edu.szu.szuschedule.util.DisplayUtil.setTranslucentStatus;

/**
 * Created by chenlin on 06/06/2017.
 */
public class BlackBoardHomeworkInfoActivity extends AppCompatActivity {
    @Bind(R.id.attachmentList)
    TextView attachmentList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bb_homework_info);
        ButterKnife.bind(this);
        setTranslucentStatus(this);

        ImageButton button_back = (ImageButton) findViewById(R.id.sub_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        attachmentList.setText(Html.fromHtml("<a href=\"/bbcswebdav/pid-516599-dt-content-rid-3765818_1/xid-3765818_1\" target=\"_blank\">实验1软件界面设计 模板 .doc</a>"));
    }
}
