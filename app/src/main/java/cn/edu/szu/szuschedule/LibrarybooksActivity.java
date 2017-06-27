package cn.edu.szu.szuschedule;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import android.widget.Toast;
import cn.edu.szu.szuschedule.object.User;
import cn.edu.szu.szuschedule.service.LibraryService;

import java.util.ArrayList;
import java.util.List;

import cn.edu.szu.szuschedule.adapter.BooksAdapter;
import cn.edu.szu.szuschedule.object.BookItem;
import cn.edu.szu.szuschedule.service.UserService;
import cn.edu.szu.szuschedule.util.LoadingUtil;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static cn.edu.szu.szuschedule.util.DisplayUtil.setTranslucentStatus;
import android.os.SystemClock;

public class LibrarybooksActivity extends AppCompatActivity {

    private List<BookItem> bookList = new ArrayList<>();
    BooksAdapter adapter;
    LoadingUtil loadingUtil;
    RecyclerView recyclerView;
    SwipeRefreshLayout book_refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        setTranslucentStatus(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        ImageButton button_back = (ImageButton) findViewById(R.id.sub_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.book_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(LibrarybooksActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        book_refresh = (SwipeRefreshLayout) findViewById(R.id.book_refresh);

        book_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(LibrarybooksActivity.this,"正在刷新",Toast.LENGTH_SHORT).show();
                LongTimeOperationTask longTimeOperationTask = new LongTimeOperationTask();
                longTimeOperationTask.execute();

            }
        });
        book_refresh.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_green_light));


        loadingUtil = new LoadingUtil(this);

        getBook();
    }

    private class LongTimeOperationTask extends AsyncTask<String,Integer,String>{
        @Override
        protected void onPreExecute() {
            book_refresh.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            SystemClock.sleep(3000);
            System.out.println("I'am working");
            getRefresh();

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            adapter.notifyDataSetChanged();
            book_refresh.setRefreshing(false);
        }
    }
    private void getBook() {
        loadingUtil.showLoading();
        LibraryService.getBorrowedBooks(LibrarybooksActivity.this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<BookItem>>() {
                    @Override
                    public void accept(ArrayList<BookItem> bookItems) throws Exception {
                        bookList = bookItems;
                        adapter = new BooksAdapter(bookList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        loadingUtil.hideLoading();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        loadingUtil.hideLoading();
                        Toast.makeText(LibrarybooksActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //更新图书信息
    private void getRefresh() {
        LibraryService.updateBorrowedBooks(LibrarybooksActivity.this).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<BookItem>>() {
                    @Override
                    public void accept(ArrayList<BookItem> bookItems) throws Exception {
                        bookList = bookItems;
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                        loadingUtil.hideLoading();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        loadingUtil.hideLoading();
                        Toast.makeText(LibrarybooksActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
