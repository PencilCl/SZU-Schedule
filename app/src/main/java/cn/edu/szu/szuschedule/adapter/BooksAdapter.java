package cn.edu.szu.szuschedule.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import cn.edu.szu.szuschedule.R;
import cn.edu.szu.szuschedule.object.bookItem;

/**
 * Created by user on 2017/6/5.
 */

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder>{

    private List<bookItem> mBookList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView borrow_Time;
        TextView book_Name;
        TextView return_DeadLine;

        public ViewHolder(View view){
            super(view);
            borrow_Time = (TextView) view.findViewById(R.id.borrow_Time);
            book_Name = (TextView) view.findViewById(R.id.book_Name);
            return_DeadLine = (TextView) view.findViewById(R.id.return_DeadLine);
        }
    }

    public BooksAdapter(List<bookItem> bookList){
        mBookList = bookList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        bookItem book = mBookList.get(position);
        holder.book_Name.setText(book.getBook_Name());
        holder.return_DeadLine.setText(book.getReturn_DeadLine());
        holder.borrow_Time.setText(book.getBorrow_Time());
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }
}
