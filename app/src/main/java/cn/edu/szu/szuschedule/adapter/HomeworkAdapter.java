package cn.edu.szu.szuschedule.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.object.HomeworkItem;

import java.util.ArrayList;

/**
 * Created by chenlin on 07/06/2017.
 */
public class HomeworkAdapter extends RecyclerView.Adapter {
    private ArrayList<HomeworkItem> homeworkItems;

    public HomeworkAdapter(ArrayList<HomeworkItem> moduleItems) {
        this.homeworkItems = moduleItems;
    }

    @Override
    public int getItemCount() {
        return homeworkItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (HomeworkAdapter.MyViewHolder) holder;
        HomeworkItem homeworkItem = homeworkItems.get(position);

        myViewHolder.courseName.setText(homeworkItem.getCourseName());
        myViewHolder.name.setText(homeworkItem.getName());
        myViewHolder.otherInfo.setText(homeworkItem.getScore() == null ? "截止日期: " + homeworkItem.getDeadline() : "得分: " + homeworkItem.getScore());
        myViewHolder.homeworkItem = homeworkItem;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView courseName;
        public TextView name;
        public TextView otherInfo;
        public HomeworkItem homeworkItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            courseName = (TextView) itemView.findViewById(R.id.courseName);
            name = (TextView) itemView.findViewById(R.id.name);
            otherInfo = (TextView) itemView.findViewById(R.id.info);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (homeworkItem.getOnClickListener() != null) {
                        homeworkItem.getOnClickListener().onClick(view, homeworkItem);
                    }
                }
            });
        }


    }
}
