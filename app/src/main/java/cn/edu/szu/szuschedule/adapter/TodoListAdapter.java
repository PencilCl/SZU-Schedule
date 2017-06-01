package cn.edu.szu.szuschedule.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.object.TodoItem;

import java.util.ArrayList;

/**
 * Created by chenlin on 30/05/2017.
 */
public class TodoListAdapter extends RecyclerView.Adapter {
    ArrayList<TodoItem> mTodoItems;

    public TodoListAdapter(ArrayList<TodoItem> todoItems) {
        mTodoItems = todoItems;
    }

    @Override
    public int getItemCount() {
        return mTodoItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo_list, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder mHolder = (ViewHolder) holder;
        mHolder.position = position;
        TodoItem todoItem = mTodoItems.get(position);

        mHolder.itemName.setText(todoItem.getItemName());
        mHolder.itemNote.setText(todoItem.getItemNote());
        mHolder.timeBegin.setText(todoItem.getTimeBegin());
        mHolder.timeEnd.setText(todoItem.getTimeEnd());
        mHolder.alertImg.setVisibility(todoItem.isShowAlert() ? View.VISIBLE : View.INVISIBLE);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView timeBegin;
        public TextView timeEnd;
        public ImageView alertImg;
        public TextView itemName;
        public TextView itemNote;
        public int position;

        public ViewHolder(View itemView) {
            super(itemView);
            timeBegin = (TextView) itemView.findViewById(R.id.timeBegin);
            timeEnd = (TextView) itemView.findViewById(R.id.timeEnd);
            alertImg = (ImageView) itemView.findViewById(R.id.alertImg);
            itemName = (TextView) itemView.findViewById(R.id.itemName);
            itemNote = (TextView) itemView.findViewById(R.id.itemNote);
        }
    }
}
