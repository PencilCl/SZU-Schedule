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
public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder>{

    List<SubjectItem> subitem;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView sname;
        TextView sstatus;
        SubjectItem subjectItem;
        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            sname = (TextView)view.findViewById(R.id.subjectname);
            sstatus = (TextView)view.findViewById(R.id.subjectstatus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (subjectItem.getOnClickListener() != null) {
                        subjectItem.getOnClickListener().onClick(v, subjectItem);
                    }
                }
            });
        }
    }

    public SubjectAdapter(List<SubjectItem> subitem){this.subitem = subitem;}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(SubjectAdapter.ViewHolder holder, int position){
        SubjectItem sitem = subitem.get(position);
        holder.sname.setText(sitem.getSubname());
        holder.sstatus.setText(sitem.getSubstatus());
        holder.subjectItem = sitem;
    }
    @Override
    public int getItemCount(){return subitem.size();}
}
