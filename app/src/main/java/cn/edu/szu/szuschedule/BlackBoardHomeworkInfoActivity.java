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
import cn.edu.szu.szuschedule.object.Attachment;
import cn.edu.szu.szuschedule.object.Homework;
import cn.edu.szu.szuschedule.service.BBService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import java.util.List;

import static cn.edu.szu.szuschedule.util.DisplayUtil.setTranslucentStatus;

/**
 * Created by chenlin on 06/06/2017.
 */
public class BlackBoardHomeworkInfoActivity extends AppCompatActivity {
    public static Homework homework;
    public static String linkTemplate = "<a href=\"%s\" target=\"_blank\">%s</a><br>";

    @Bind(R.id.attachmentList)
    TextView attachmentList;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.description)
    TextView description;
    @Bind(R.id.deadline)
    TextView deadline;
    @Bind(R.id.score)
    TextView score;
    @Bind(R.id.attachment)
    TextView attachment;

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

        name.setText(homework.getName());
        description.setText(Html.fromHtml(homework.getDescription()));
        deadline.setText("截止日期： " + homework.getDeadline());
        if (homework.getScore() != -1) {
            score.setVisibility(View.VISIBLE);
            score.setText("得分: " + homework.getScore());
        } else {
            score.setVisibility(View.GONE);
        }

        List<Attachment> attachments = BBService.getAttachment(homework);
        if (attachments.size() == 0) {
            attachmentList.setVisibility(View.GONE);
            attachment.setVisibility(View.GONE);
            return ;
        }
        StringBuilder attachmentLink = new StringBuilder();
        for (Attachment attachment : attachments) {
            attachmentLink.append(String.format(linkTemplate, attachment.getUrl(), attachment.getName()));
        }
        attachmentList.setText(Html.fromHtml(attachmentLink.toString()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
