package cn.edu.szu.szuschedule;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.szu.szuschedule.adapter.BooksAdapter;
import cn.edu.szu.szuschedule.object.bookItem;

public class LibrarybooksActivity extends AppCompatActivity {

    private List<bookItem> bookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.books_content);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        initBooks();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.book_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        BooksAdapter adapter = new BooksAdapter(bookList);
        recyclerView.setAdapter(adapter);

    }

    private void initBooks() {
        for (int i = 0; i < 3; i++) {
            bookItem book = new bookItem("Linux私房菜", "2017年4月20日", "2017年7月20日");
            bookList.add(book);
        }
    }
}
