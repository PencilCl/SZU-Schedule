package cn.edu.szu.szuschedule;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import cn.edu.szu.szuschedule.service.LibraryService;

import java.util.List;

import cn.edu.szu.szuschedule.adapter.BooksAdapter;
import cn.edu.szu.szuschedule.object.BookItem;

import static cn.edu.szu.szuschedule.util.DisplayUtil.setTranslucentStatus;

public class LibraryBooksActivity extends AppCompatActivity implements LibraryService.OnDataChangedListener {

    private List<BookItem> bookList;
    BooksAdapter adapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(LibraryBooksActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.book_refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LibraryService.refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_green_light));

        LibraryService.addOnDataChangedListener(this);
    }

    @Override
    public void onBookItemsChanged(List<BookItem> bookItems) {
        if (bookList == null) {
            bookList = bookItems;
            adapter = new BooksAdapter(bookList);
            recyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        LibraryService.removeOnDataChangedListener(this);
        super.onDestroy();
    }
}
