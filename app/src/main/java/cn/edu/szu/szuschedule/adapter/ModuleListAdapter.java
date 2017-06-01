package cn.edu.szu.szuschedule.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.object.ModuleItem;
import cn.edu.szu.szuschedule.view.SquareButton;

import java.util.ArrayList;

/**
 * Created by chenlin on 01/06/2017.
 */
public class ModuleListAdapter extends RecyclerView.Adapter {
    private ArrayList<ModuleItem> moduleItems;

    public ModuleListAdapter(ArrayList<ModuleItem> moduleItems) {
        this.moduleItems = moduleItems;
    }

    /**
     * add module
     */
    public void addModule(ModuleItem moduleItem) {
        moduleItems.add(moduleItem);
    }

    /**
     * remove module by object
     * @param moduleItem moduleItem object
     */
    public void removeModule(ModuleItem moduleItem) {
        moduleItems.remove(moduleItem);
    }

    /**
     * remove module by position
     * @param position
     */
    public void removeModule(int position) {
        moduleItems.remove(position);
    }

    @Override
    public int getItemCount() {
        return moduleItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_module_list, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        ModuleItem moduleItem = moduleItems.get(position);

        myViewHolder.module.setText(moduleItem.getModuleNameRes());
        myViewHolder.module.setBackgroundResource(moduleItem.getBgRes());
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public SquareButton module;

        public MyViewHolder(View itemView) {
            super(itemView);
            module = (SquareButton) itemView.findViewById(R.id.module);
        }
    }
}
