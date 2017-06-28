package cn.edu.szu.szuschedule;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.edu.szu.szuschedule.adapter.ViewPagerAdapter;
import cn.edu.szu.szuschedule.fragment.HomeFragment;
import cn.edu.szu.szuschedule.fragment.ScheduleFragment;
import cn.edu.szu.szuschedule.fragment.UserFragment;

import static cn.edu.szu.szuschedule.util.DisplayUtil.setTranslucentStatus;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.bottomNavigation)
    BottomNavigationView bottomNavigationView;

    MenuItem currentBottomMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTranslucentStatus(this);

        init();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override
    public void onPageSelected(int position) {
        if (currentBottomMenuItem != null) {
            currentBottomMenuItem.setChecked(false);
        } else {
            bottomNavigationView.getMenu().getItem(0).setChecked(false);
        }

        currentBottomMenuItem = bottomNavigationView.getMenu().getItem(position);
        currentBottomMenuItem.setChecked(true);
    }
    @Override
    public void onPageScrollStateChanged(int state) {}

    private void init() {
        viewPager.addOnPageChangeListener(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return onBottomNavigationItemSelected(item);
            }
        });
        setupViewPager(viewPager);
    }

    private boolean onBottomNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_nav_home:
                viewPager.setCurrentItem(0);
                break;
            case R.id.bottom_nav_time:
                viewPager.setCurrentItem(1);
                break;
            case R.id.bottom_nav_user:
                viewPager.setCurrentItem(2);
                break;
            default:
                return false;
        }
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new ScheduleFragment());
        adapter.addFragment(new UserFragment());
        viewPager.setAdapter(adapter);
    }
}
