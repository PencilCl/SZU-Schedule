package cn.edu.szu.szuschedule;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.ImageButton;
import cn.edu.szu.szuschedule.adapter.BooksAdapter;
import cn.edu.szu.szuschedule.object.bookItem;

import static cn.edu.szu.szuschedule.util.DisplayUtil.setTranslucentStatus;

public class LibrarybooksActivity extends AppCompatActivity {

    private List<bookItem> bookList = new ArrayList<>();

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
