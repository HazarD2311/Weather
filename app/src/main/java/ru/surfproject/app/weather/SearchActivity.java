package ru.surfproject.app.weather;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import ru.surfproject.app.weather.Adapters.FragmentsAdapter;
import ru.surfproject.app.weather.Fragments.FragmentMap;
import ru.surfproject.app.weather.Fragments.FragmentSearch;


public class SearchActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    FragmentMap fragmentMap;
    FragmentSearch fragmentSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Инициализируем actionBar, для того, чтобы добавить в него кнопку назад
        ActionBar actionBar = getDelegate().getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        fragmentMap = new FragmentMap();
        fragmentSearch = new FragmentSearch();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    private void setupViewPager(ViewPager viewPager) {
        FragmentsAdapter adapter = new FragmentsAdapter(getSupportFragmentManager());
        adapter.addFragment(fragmentSearch, "Поиск");
        adapter.addFragment(fragmentMap, "Карта");
        viewPager.setAdapter(adapter);
    }

    // Метод слушает нажатые кнопки в actionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Завершаем текущее активити
        }
        return super.onOptionsItemSelected(item);
    }
}
