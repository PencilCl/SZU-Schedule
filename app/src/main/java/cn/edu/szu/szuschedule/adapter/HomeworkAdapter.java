package cn.edu.szu.szuschedule.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.object.Homework;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenlin on 07/06/2017.
 */
public class HomeworkAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private List<Homework> homeworkItems;
    private RecyclerView mRecyclerView;
    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onClick(int position, View view, Homework homework);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public HomeworkAdapter(List<Homework> moduleItems) {
        this.homeworkItems = moduleItems;
    }

    @Override
    public int getItemCount() {
        return homeworkItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework, null);
        view.setOnClickListener(this);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (HomeworkAdapter.MyViewHolder) holder;
        Homework homeworkItem = homeworkItems.get(position);

        myViewHolder.courseName.setText(homeworkItem.getSubjectItem().getSubjectName());
        myViewHolder.name.setText(homeworkItem.getName());
        myViewHolder.otherInfo.setText(homeworkItem.isFinished() ? "得分: " + (homeworkItem.getScore() == -1 ? "待批改" : homeworkItem.getScore()) : "截止日期: " + homeworkItem.getDeadline());
        myViewHolder.homeworkItem = homeworkItem;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View v) {
        if (this.onClickListener != null) {
            int position = mRecyclerView.getChildAdapterPosition(v);
            this.onClickListener.onClick(position, v, homeworkItems.get(position));
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView courseName;
        public TextView name;
        public TextView otherInfo;
        public Homework homeworkItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            courseName = (TextView) itemView.findViewById(R.id.courseName);
            name = (TextView) itemView.findViewById(R.id.name);
            otherInfo = (TextView) itemView.findViewById(R.id.info);
        }
    }
}
