package cn.edu.szu.szuschedule.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.TextView;
import cn.edu.szu.szuschedule.R;
import android.view.ViewGroup;
import cn.edu.szu.szuschedule.object.SubjectItem;

import java.util.List;

/**
 * Created by jazzyzhong on 2017/6/7.
 */
public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> implements View.OnClickListener {

    List<SubjectItem> subjectItems;
    private RecyclerView mRecyclerView;
    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onClick(int position, View view, SubjectItem subjectItem);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public SubjectAdapter(List<SubjectItem> subjectItems){this.subjectItems = subjectItems;}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(SubjectAdapter.ViewHolder holder, int position){
        SubjectItem subjectItem = subjectItems.get(position);
        holder.name.setText(subjectItem.getSubjectName());
        holder.status.setText(subjectItem.getStatus());
        holder.subjectItem = subjectItem;
    }
    @Override
    public int getItemCount(){return subjectItems.size();}

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View v) {
        if (this.onClickListener != null) {
            int position = mRecyclerView.getChildAdapterPosition(v);
            this.onClickListener.onClick(position, v, subjectItems.get(position));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView name;
        TextView status;
        SubjectItem subjectItem;
        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            name = (TextView)view.findViewById(R.id.subjectname);
            status = (TextView)view.findViewById(R.id.subjectstatus);
        }
    }
}
