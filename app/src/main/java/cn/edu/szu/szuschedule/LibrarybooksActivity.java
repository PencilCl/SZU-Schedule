package cn.edu.szu.szuschedule;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ImageButton;

import cn.edu.szu.szuschedule.service.UserService;
import com.lzy.okgo.OkGo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.szu.szuschedule.adapter.BooksAdapter;
import cn.edu.szu.szuschedule.object.BookItem;
import cn.edu.szu.szuschedule.object.User;
import cn.edu.szu.szuschedule.util.LoadingUtil;

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

        new LibraryTask().execute();

    }

    private class LibraryTask extends AsyncTask<Void, Integer, Boolean> {
        private final String booksUrl = "http://opac.lib.szu.edu.cn/opac/user/bookborrowed.aspx";
        private final String reg = "<TD width=\"10%\">(.*?)</TD>\\s*?" +
                "<TD width=\"35%\"><[\\s\\S]*?>([\\s\\S]*?)[／/][\\s\\S]*?</a>" +
                "[\\s\\S]*?<TD width=\"10%\">(.*?)</TD>";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingUtil.showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            CookieManager cookieManager = CookieManager.getInstance();
            if(cookieManager.getCookie(booksUrl) == null) {
                return false;
            }
            try {
                okhttp3.Response response = OkGo.get(booksUrl)
                        .headers("Cookie", cookieManager.getCookie(booksUrl))
                        .execute();
                String html = response.body().string();
                Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(html);
                while(matcher.find()) {
                    BookItem book = new BookItem(UserService.getCurrentUser(), matcher.group(2), matcher.group(3), matcher.group(1));
                    bookList.add(book);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            loadingUtil.hideLoading();
            if(aBoolean) {
                adapter.notifyDataSetChanged();
            }
        }

    }
}
