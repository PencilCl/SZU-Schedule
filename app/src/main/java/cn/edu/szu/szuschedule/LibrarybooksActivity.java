package cn.edu.szu.szuschedule;

import android.os.Bundle;
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

public class LibrarybooksActivity extends AppCompatActivity {

    private List<BookItem> bookList = new ArrayList<>();
    BooksAdapter adapter;
    LoadingUtil loadingUtil;

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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.book_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(LibrarybooksActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BooksAdapter(bookList);
        recyclerView.setAdapter(adapter);

        loadingUtil = new LoadingUtil(this);

        getBook();
    }

    private void getBook() {
        loadingUtil.showLoading();
        User user = UserService.getCurrentUser();
        LibraryService.loginLibrary(user.getAccount(), user.getPassword())
                .flatMap(new Function<String, ObservableSource<ArrayList<BookItem>>>() {
                    @Override
                    public ObservableSource<ArrayList<BookItem>> apply(String s) throws Exception {
                        return LibraryService.getBorrowedBooks(LibrarybooksActivity.this);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<BookItem>>() {
                    @Override
                    public void accept(ArrayList<BookItem> bookItems) throws Exception {
                        bookList = bookItems;
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
}
