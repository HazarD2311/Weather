package ru.surfproject.app.weather.ui.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.adapter.FragmentsAdapter;
import ru.surfproject.app.weather.ui.fragment.SearchFragment;
import ru.surfproject.app.weather.ui.fragment.SearchMapFragment;


public class SearchActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    SearchMapFragment fragmentMap;
    SearchFragment fragmentSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Инициализируем ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Добавление кнопки назад в toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        fragmentMap = new SearchMapFragment();
        fragmentSearch = new SearchFragment();
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
