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
        TextView sname;
        TextView sstatus;
        ViewHolder(View view) {
            super(view);
            sname = (TextView)view.findViewById(R.id.subjectname);
            sstatus = (TextView)view.findViewById(R.id.subjectstatus);
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
    }
    @Override
    public int getItemCount(){return subitem.size();}
}
